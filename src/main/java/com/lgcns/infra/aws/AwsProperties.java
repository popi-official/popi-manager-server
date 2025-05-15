package com.lgcns.infra.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws")
public record AwsProperties(String region, String accessKey, String secretKey) {}
