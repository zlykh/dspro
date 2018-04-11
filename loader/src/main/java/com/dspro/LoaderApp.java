package com.dspro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by dmitry.zlykh
 */
@EnableScheduling
@PropertySource(value = {
        "classpath:conf/defaults.properties",
        "classpath:${config_path}"
})
@SpringBootApplication
public class LoaderApp {


    public static void main(String[] args) {
        SpringApplication.run(LoaderApp.class, args);
    }


}
