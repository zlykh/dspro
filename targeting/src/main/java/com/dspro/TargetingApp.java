package com.dspro;

import com.dspro.enums.MQConst;
import com.dspro.logic.MatcherProcess;
import com.dspro.logic.RatingProperties;
import com.dspro.logic.rule.AgeRatingRule;
import com.dspro.logic.rule.RatingRule;
import com.dspro.logic.rule.TagsRatingRule;
import com.dspro.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmitry.zlykh
 */
@EnableScheduling
@PropertySource(value = {
        "classpath:conf/defaults.properties",
        "classpath:${config_path}"
})
@SpringBootApplication
@Configuration
public class TargetingApp {
    private static final Logger log = LoggerFactory.getLogger(TargetingApp.class);

    @Autowired
    ProfileService profileService;

    @Autowired
    RabbitTemplate template;

    @Autowired
    private RatingProperties ratingProperties;

    public static void main(String[] args) {
        SpringApplication.run(TargetingApp.class, args);
    }

    //    @Scheduled(cron = "0 0 0 * * *")
    public void targetingJob() {

        template.convertAndSend(MQConst.QUEUE_MATCHER, MQConst.CMD_MATCHER_BATCH);
        template.convertAndSend(MQConst.QUEUE_MATCHER, "123"); //ctn
        template.convertAndSend(MQConst.QUEUE_MGM_PROFILES, "flushdb");
        //  ovTargetingProcess.startSingle();
    }


    @Bean
    public MatcherProcess OVTargetingProcess() {
        MatcherProcess process = new MatcherProcess();
        List<RatingRule> rules = new ArrayList<>();
        rules.add(new TagsRatingRule(ratingProperties.getTagsFraction()));
        rules.add(new AgeRatingRule(ratingProperties.getAgeFraction()));
//
        process.setRatingRules(rules);
        process.setProfileService(profileService);

        return process;
    }


}
