package com.dspro.dao;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/com/dspro/dao/atc/testContext.xml")
public abstract class BaseRedisTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Before
    public void setUp(){
        stringRedisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return null;
        });
    }
}
