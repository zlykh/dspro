package com.dspro;

import org.junit.Before;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

public abstract class BaseRedisTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    private static final Logger log = LoggerFactory.getLogger(BaseRedisTest.class);


    @Before
    public void setUp() {

        stringRedisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            redisConnection.flushDb();
            return null;
        });
    }
}
