package com.dspro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private Integer redisPort;
    @Value("${redis.password}")
    private String redisPwd;

    @Bean
    @Profile("default")
    public RedisConnectionFactory redisDefaultConnectionFactory() {
        JedisConnectionFactory redis = new JedisConnectionFactory();
        redis.setUsePool(true);
        redis.setHostName(redisHost);
        redis.setPort(redisPort);
        redis.setPassword(redisPwd);
        return redis;
    }

    @Bean
    @Profile("heroku")
    public RedisConnectionFactory redisConnectionFactory() throws URISyntaxException {
        JedisConnectionFactory redis = new JedisConnectionFactory();
        String redisUrl = System.getenv("REDIS_URL");
        URI redisUri = new URI(redisUrl);
        redis.setUsePool(true);
        redis.setHostName(redisUri.getHost());
        redis.setPort(redisUri.getPort());
        redis.setPassword(redisUri.getUserInfo().split(":", 2)[1]);
        return redis;
    }
}
