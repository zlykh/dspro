package com.dspro.logic;

import com.dspro.enums.MQConst;
import com.dspro.enums.RedisConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MatcherQueueConsumer {
    private static final Logger log = LoggerFactory.getLogger(MatcherQueueConsumer.class);

    private StringRedisTemplate redisTemplate;
    private MatcherProcess matcherProcess;

    @Autowired
    public void setMatcherProcess(MatcherProcess matcherProcess) {
        this.matcherProcess = matcherProcess;
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*
     * MQ LISTENERS
     */

    @RabbitListener(queues = MQConst.QUEUE_MATCHER)
    public void matcherListener(String ctnOrBatch) {
        log.trace("[{}]: {}", MQConst.QUEUE_MATCHER, ctnOrBatch);

        switch (ctnOrBatch) {
            case MQConst.CMD_MATCHER_BATCH:
                matcherProcess.startBatch();
                break;
            default:
                matcherProcess.startSingle(ctnOrBatch);
                break;
        }

    }

    // if 2 per queue, then round robin
    @RabbitListener(queues = MQConst.QUEUE_MGM_PROFILES)
    public void profileMgmListener(String action) {
        log.trace("[{}]: {}", MQConst.QUEUE_MGM_PROFILES, action);
        switch (action) {
            case RedisConst.MQ_FLUSHDB:
                flushDB();
                break;
            default:
                log.warn("no action for this command");
                break;
        }
    }

    private void flushDB() {
        redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return null;
        });
    }
}
