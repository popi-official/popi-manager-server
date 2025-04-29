package com.lgcns.infra.properties;

import com.lgcns.infra.redis.RedisProperties;
import com.lgcns.infra.s3.S3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({RedisProperties.class, S3Properties.class})
@Configuration
public class PropertiesConfig {}
