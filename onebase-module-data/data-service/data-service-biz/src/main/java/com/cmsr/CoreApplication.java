package com.cmsr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.cmsr.listener.EhCacheStartListener;

@SpringBootApplication(exclude = {QuartzAutoConfiguration.class, org.springdoc.webmvc.ui.SwaggerConfig.class})
@EnableCaching
@EnableScheduling
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication context = new SpringApplication(CoreApplication.class);
        context.addInitializers(new EhCacheStartListener());
        context.run(args);
    }
}