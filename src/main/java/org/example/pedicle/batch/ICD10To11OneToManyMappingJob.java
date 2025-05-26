package org.example.pedicle.batch;

import org.apache.commons.lang3.EnumUtils;
import org.example.pedicle.domain.ICD10Info;
import org.example.pedicle.domain.ICD10To11Mapping;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Optional;

@Configuration
@EnableBatchProcessing
public class ICD10To11OneToManyMappingJob {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    public ICD10To11OneToManyMappingJob(@Autowired JobRepository jobRepository,
                                        @Autowired DataSource dataSource,
                                        @Autowired PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job icd10To11OneToManyMappingInsertJob() {
        return new JobBuilder("icd10To11OneToManyMappingInsertJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(icd10To11OneToManyMappingInsertStep())
                .build();
    }

    @Bean
    public Step icd10To11OneToManyMappingInsertStep() {
        return new StepBuilder("icd10To11OneToManyMappingInsertStep", jobRepository)
                .<ICD10MappingOneToManyCSV, ICD10To11Mapping>chunk(1000, transactionManager)
                .reader(icd10To11OneToManyMappingReader())
                .processor(icd10To11OneToManyMappingProcessor())
                .writer(icd10To11OneToManyMappingWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<ICD10MappingOneToManyCSV> icd10To11OneToManyMappingReader() {
        return new FlatFileItemReaderBuilder<ICD10MappingOneToManyCSV>()
                .name("icd10To11OneToManyMappingReader")
                .resource(new FileSystemResource("src/main/resources/10To11MapToMultipleCategories.csv"))
                .delimited()
                .delimiter(",")
                .names("icd10ClassKind",
                        "icd10DepthInKind",
                        "icd10Code",
                        "icd10Chapter",
                        "icd10Title",
                        "icd11ClassKind",
                        "icd11DepthInKind",
                        "icd11FoundationUri",
                        "icd11LinearizationUri",
                        "icd11Code",
                        "icd11Chapter",
                        "icd11Title")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ICD10MappingOneToManyCSV.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<ICD10MappingOneToManyCSV, ICD10To11Mapping> icd10To11OneToManyMappingProcessor() {
        return new ICD10To11OneToManyMappingJob.Icd10To11OneToManyCsvToEntityMapper();
    }

    @Bean
    public JdbcBatchItemWriter<ICD10To11Mapping> icd10To11OneToManyMappingWriter() {
        return new JdbcBatchItemWriterBuilder<ICD10To11Mapping>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO icd10_to_11_mapping (
                            icd10depth_in_kind,
                            icd10class_kind,
                            icd10code,
                            icd10chapter,
                            icd10title,
                            icd11depth_in_kind,
                            icd11class_kind,
                            icd11entity_id,
                            icd11foundation_uri,
                            icd11linearization_uri,
                            icd11title,
                            icd11code,
                            icd11chapter
                        ) VALUES (
                            :icd10DepthInKind,
                            :icd10ClassKind,
                            :icd10Code,
                            :icd10Chapter,
                            :icd10Title,
                            :icd11DepthInKind,
                            :icd11ClassKind,
                            :icd11EntityId,
                            :icd11FoundationUri,
                            :icd11LinearizationUri,
                            :icd11Title,
                            :icd11Code,
                            :icd11Chapter
                    )
                    """)
                .itemSqlParameterSourceProvider((ICD10To11Mapping item) -> {
                    BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(item);
                    paramSource.registerSqlType("icd10ClassKind", Types.VARCHAR);
                    paramSource.registerSqlType("icd11ClassKind", Types.VARCHAR);
                    return paramSource;
                })
                .build();
    }

    public static class Icd10To11OneToManyCsvToEntityMapper implements ItemProcessor<ICD10MappingOneToManyCSV, ICD10To11Mapping> {
        @Override
        public ICD10To11Mapping process(ICD10MappingOneToManyCSV item) {
            return ICD10To11Mapping.builder()
                    .icd10DepthInKind(item.getIcd10DepthInKind())
                    .icd10ClassKind(EnumUtils.getEnumIgnoreCase(ICD10To11Mapping.ClassKind.class, item.getIcd10ClassKind()))
                    .icd10Code(item.getIcd10Code())
                    .icd10Chapter(item.getIcd10Chapter())
                    .icd10Title(item.getIcd10Title())
                    .icd11DepthInKind(item.getIcd10DepthInKind())
                    .icd11ClassKind(EnumUtils.getEnumIgnoreCase(ICD10To11Mapping.ClassKind.class, item.getIcd10ClassKind()))
                    .icd11EntityId(Optional.ofNullable(item.getIcd11FoundationUri()).map(u -> {
                                String[] parts = u.split("/");
                                return parts[parts.length - 1];
                            })
                            .orElse(null))
                    .icd11FoundationUri(item.getIcd11FoundationUri())
                    .icd11LinearizationUri(item.getIcd11LinearizationUri())
                    .icd11Title(item.getIcd11Title())
                    .icd11Code(item.getIcd11Code())
                    .icd11Chapter(item.getIcd11Chapter())
                    .build();
        }
    }

}
