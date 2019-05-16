package com.tianwen.springcloud.ecrapi;

import com.tianwen.springcloud.ecrapi.util.CORSFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.tianwen.springcloud.commonapi.utils.BeanHolder;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.annotation.Resource;
import java.util.Locale;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"com.tianwen.springcloud.**.**.swagger2", "com.tianwen.springcloud.**.**.controller",
    "com.tianwen.springcloud.**.**.service", "com.tianwen.springcloud.**.**.dao", "com.tianwen.springcloud.**.**.log",
    "com.tianwen.springcloud.**.**.configuration", "com.tianwen.springcloud.commonapi", "com.tianwen.springcloud.**.**.component",
    "com.tianwen.springcloud.**.**.task"})
@EnableFeignClients(basePackages = {"com.tianwen.springcloud.**.**.**"})
@EnableAutoConfiguration
@Import(value = {BeanHolder.class})
public class TwSpringCloudECRapiApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(TwSpringCloudECRapiApplication.class, args);
        System.out.println("TwSpringCloudECRapiApplication after");
    }

    @Bean
    public FilterRegistrationBean corsFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new CORSFilter());
        return bean;
    }
}
