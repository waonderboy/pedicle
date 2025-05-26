package org.example.pedicle.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobStarter implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job icd10MappingInfoInsertJob; // Job 이름 주의
    private final Job icd10To11OneToManyMappingInsertJob; // Job 이름 주의
    private final JobExplorer jobExplorer;

    @Value("${spring.batch.job.enabled}")
    private boolean jobStart;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder(jobExplorer)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
//        System.exit(0);
        if (jobStart){
//            jobLauncher.run(icd10MappingInfoInsertJob, jobParameters);
            jobLauncher.run(icd10To11OneToManyMappingInsertJob, jobParameters);
        }
    }
}

