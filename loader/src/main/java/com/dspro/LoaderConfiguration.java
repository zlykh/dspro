package com.dspro;

import com.dspro.logic.RenameTasklet;
import com.dspro.logic.SqlResourceReader;
import com.dspro.logic.profile.CtnProfileWriter;
import com.dspro.logic.profile.ProfileBatchMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class LoaderConfiguration {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job customerReportJob() {
        return jobBuilderFactory.get("loadProfile")
                .start(chunkStep())
                .next(taskletStep())
                .build();
    }

    @Bean
    public Step taskletStep() {
        RenameTasklet renameTasklet = new RenameTasklet();
        renameTasklet.source = "_prof";
        renameTasklet.dest = "prof";
        return stepBuilderFactory.get("rename")
                .tasklet(renameTasklet)
                .build();
    }


    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get("chunkStep")
                .chunk(10_000)
                .reader(sqlReader())
                .writer(profileWriter())
                .build();
    }

    @StepScope
    @Bean
    public SqlResourceReader sqlReader() {
        SqlResourceReader reader = new SqlResourceReader<>();
        reader.setSqlFileName("sql/loadProfile.sql");
        reader.setFetchSize(10_000);
        reader.setDataSource(dataSource);
        reader.setRowMapper(new ProfileBatchMapper());

        return reader;
    }

    @StepScope
    @Bean
    public ItemWriter profileWriter() {
        CtnProfileWriter writer = new CtnProfileWriter();
        writer.setRedisTemplate(stringRedisTemplate);
        return writer;
    }

}
