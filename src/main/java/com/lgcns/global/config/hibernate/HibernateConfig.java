package com.lgcns.global.config.hibernate;

import com.lgcns.global.interceptor.DirtyCheckInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public DirtyCheckInterceptor myInterceptor() {
        return new DirtyCheckInterceptor();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            DirtyCheckInterceptor dirtyCheckInterceptor) {
        return props -> props.put("hibernate.session_factory.interceptor", dirtyCheckInterceptor);
    }
}
