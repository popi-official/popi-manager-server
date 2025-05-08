package com.lgcns.infra.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws")
public record S3Properties(
        String region, String accessKey, String secretKey, String bucket, String endpoint) {}
