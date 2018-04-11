package com.dspro.controller;


import com.dspro.domain.Profile;
import com.dspro.logic.profile.ProfileBatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by dmitry.zlykh
 */
@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    @Autowired
    JobRegistry jobRegistry;
    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @RequestMapping(value = "/jobp", method = RequestMethod.GET)
    public void runjob2() throws Exception {
        Job job = jobRegistry.getJob("loadProfile");
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

        log.debug("EXIT CODE {}", jobExecution.getExitStatus());
    }

    @RequestMapping(value = "/jobt", method = RequestMethod.GET)
    public void runjob23() throws Exception {
        Job job = jobRegistry.getJob("loadTags");
        JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

        log.debug("EXIT CODE {}", jobExecution.getExitStatus());
    }

    @RequestMapping(value = "/prof", method = RequestMethod.GET)
    @ResponseBody
    public Profile runJob1() {
        Profile p = new Profile();
        p.age = 32;
        p.ctn = "123";
        return p;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> runJob() {
        List<Profile> list = null;
        try {
            list = jdbcTemplate.query("select * from agg_profile where rownum < 10", new ProfileBatchMapper());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(200).body("");
    }

}
