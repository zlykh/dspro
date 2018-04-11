package com.dspro;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableScheduling
@PropertySource(value = {
        "classpath:conf/defaults.properties",
        "classpath:${config_path}"
})
@SpringBootApplication
public class WebServiceApp {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebServiceApp.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

}
