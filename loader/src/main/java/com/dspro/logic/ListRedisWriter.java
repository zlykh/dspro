package com.dspro.logic;

import com.dspro.StringRedisCallback;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;


public abstract class ListRedisWriter<T> implements ItemWriter<T> {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected abstract void process(T item, StringRedisConnection redis);

    @Override
    public void write(List<? extends T> list) throws Exception {
        redisTemplate.executePipelined(new StringRedisCallback<Object>() {
            @Override
            public Object doInRedis(StringRedisConnection redis) {
                for (T item : list) {
                    process(item, redis);
                }
                return null;
            }
        });
    }
}
