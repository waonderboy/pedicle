package org.example.pedicle.batch;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.example.pedicle.domain.ICD10Info;
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
import java.util.Locale;
import java.util.Optional;

// 5. Batch Configuration
@Configuration
//@RequiredArgsConstructor
@EnableBatchProcessing
public class ICD10BatchJob {
    private final JobRepository jobRepository;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    public ICD10BatchJob(@Autowired JobRepository jobRepository,
                         @Autowired DataSource dataSource,
                         @Autowired PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job icd10MappingInfoInsertJob() {
        return new JobBuilder("icd10MappingInfoInsertJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(icd10MappingInfoInsertStep())
                .build();
    }

    @Bean
    public Step icd10MappingInfoInsertStep() {
        return new StepBuilder("icd10MappingInfoInsertStep", jobRepository)
                .<ICD10MappingCSV, ICD10Info>chunk(1000, transactionManager)
                .reader(icdMappingReader())
                .processor(icdMappingProcessor())
                .writer(icdMappingWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<ICD10MappingCSV> icdMappingReader() {
        return new FlatFileItemReaderBuilder<ICD10MappingCSV>()
                .name("icdMappingReader")
                .resource(new FileSystemResource("src/main/resources/icd_mapping.csv"))
                .delimited()
                .delimiter(",")
                .names("icd10ClassKind","icd10DepthInKind","icd10Code","icd10Chapter","icd10Title","icd11Uri","icd11Title","relationType")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ICD10MappingCSV.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public ItemProcessor<ICD10MappingCSV, ICD10Info> icdMappingProcessor() {
        return new IcdMappingCsvToEntityMapper();
    }

    @Bean
    public JdbcBatchItemWriter<ICD10Info> icdMappingWriter() {
        return new JdbcBatchItemWriterBuilder<ICD10Info>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("""
                        INSERT INTO icd10_info (
                            icd10class_kind,
                            icd10depth_in_kind,
                            icd10code,
                            icd10chapter,
                            icd10title,
                            icd11uri,
                            icd11entity_id,
                            icd11title,
                            relation_type
                        ) VALUES (
                            :icd10ClassKind,
                            :icd10DepthInKind,
                            :icd10Code,
                            :icd10Chapter,
                            :icd10Title,
                            :icd11Uri,
                            :icd11EntityId,
                            :icd11Title,
                            :relationType
                        )
                    """)
                .itemSqlParameterSourceProvider((ICD10Info item) -> {
                    BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(item);
                    paramSource.registerSqlType("relationType", Types.VARCHAR);
                    paramSource.registerSqlType("icd10ClassKind", Types.VARCHAR);
                    return paramSource;
                })
                .build();
    }

    public static class IcdMappingCsvToEntityMapper implements ItemProcessor<ICD10MappingCSV, ICD10Info> {
        @Override
        public ICD10Info process(ICD10MappingCSV item) {
            return ICD10Info.builder()
                    .icd10ClassKind(EnumUtils.getEnumIgnoreCase(ICD10Info.ICD10ClassKind.class, item.getIcd10ClassKind()))
                    .icd10DepthInKind(item.getIcd10DepthInKind())
                    .icd10Code(item.getIcd10Code())
                    .icd10Chapter(item.getIcd10Chapter())
                    .icd10Title(item.getIcd10Title())
                    .icd11Uri(item.getIcd11Uri())
                    .icd11EntityId(Optional.ofNullable(item.getIcd11Uri()).map(u -> {
                                String[] parts = u.split("/");
                                return parts[parts.length - 1];
                            })
                            .orElse(null)) //  파싱해야함
                    .icd11Title(item.getIcd11Title())
                    .relationType(EnumUtils.getEnumIgnoreCase(ICD10Info.RelationType.class, item.getRelationType()))
                    .build();
        }
    }

}
