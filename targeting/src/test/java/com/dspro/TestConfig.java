package com.dspro;


import org.springframework.context.annotation.*;
import org.springframework.test.context.TestPropertySource;

@Configuration

@TestPropertySource(value = {"classpath:conf/local-test.properties"})
@ComponentScan(basePackages = {"com.dspro", "org.springframework.data.redis"})
public class TestConfig {

}
