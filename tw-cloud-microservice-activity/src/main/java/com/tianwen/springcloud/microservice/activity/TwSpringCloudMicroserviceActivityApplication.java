package com.tianwen.springcloud.microservice.activity;

import com.tianwen.springcloud.commonapi.utils.BeanHolder;
import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.tianwen.springcloud.**.**.swagger2", "com.tianwen.springcloud.**.**.controller",
    "com.tianwen.springcloud.**.**.service", "com.tianwen.springcloud.**.**.dao", "com.tianwen.springcloud.**.**.log",
    "com.tianwen.springcloud.**.**.configuration", "com.tianwen.springcloud.commonapi",
    "com.tianwen.springcloud.datasource.base"})
@ImportResource(locations = {"classpath:springContext.xml"})
@Import(value = {BeanHolder.class})
@EnableAutoConfiguration
public class TwSpringCloudMicroserviceActivityApplication
{
    public static void main(String[] args)
    {
        System.out.println("TwSpringCloudMicroserviceActivityApplication before");

        BasicConfigurator.configure();
        SpringApplication.run(TwSpringCloudMicroserviceActivityApplication.class, args);

        System.out.println("TwSpringCloudMicroserviceActivityApplication after");
    }
}
