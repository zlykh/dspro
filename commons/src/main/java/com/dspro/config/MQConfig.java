package com.dspro.config;


import com.dspro.enums.MQConst;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


@Configuration
public class MQConfig {

    @Bean
    @Profile("default")
    public ConnectionFactory ConnectionFactoryDefault() {
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername("test");
        factory.setPassword("test");
        factory.setHost("localhost");

        return factory;
    }

    @Bean
    @Profile("heroku")
    public ConnectionFactory ConnectionFactory() throws URISyntaxException{
        final URI rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));

        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
        factory.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
        factory.setHost(rabbitMqUrl.getHost());
        factory.setPort(rabbitMqUrl.getPort());
        factory.setVirtualHost(rabbitMqUrl.getPath().substring(1));

        return factory;
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public List<Queue> queue() {
        return Arrays.asList(
                new Queue(MQConst.QUEUE_MATCHER,true),
                new Queue(MQConst.QUEUE_MGM_PROFILES,true));
    }

}
