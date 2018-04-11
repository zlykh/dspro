package com.dspro;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;

public abstract class StringRedisCallback<T> implements RedisCallback<T> {
    public StringRedisCallback() {
    }

    public T doInRedis(RedisConnection redis) throws DataAccessException {
        return this.doInRedis((new DefaultStringRedisConnection(redis)));
    }

    public abstract T doInRedis(StringRedisConnection redis);
}
