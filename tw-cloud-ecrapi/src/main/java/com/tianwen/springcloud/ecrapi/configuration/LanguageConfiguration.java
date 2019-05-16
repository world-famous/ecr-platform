package com.tianwen.springcloud.ecrapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tianwen.springcloud.commonapi.interceptor.LanguageInterceptor;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;


/**
 * 注册拦截器，根据请求头的X-Client-Language参数设置语言环境
 * 
 * @author  Huang
 * @version  [版本号, 2018年5月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Configuration
public class LanguageConfiguration extends WebMvcConfigurerAdapter{

    /**
     * 注册 拦截器
     */@Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
    }
}