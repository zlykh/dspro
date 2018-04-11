package com.dspro.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RenameTasklet implements Tasklet {
    private final Logger log = LoggerFactory.getLogger(RenameTasklet.class.getName());

    public String source;
    public String dest;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        if (redisTemplate.hasKey(source)) {
            redisTemplate.rename(source, dest);
        }

        return RepeatStatus.FINISHED;
    }
}
