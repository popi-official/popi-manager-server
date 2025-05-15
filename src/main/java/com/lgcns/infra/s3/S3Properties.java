package com.lgcns.infra.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws.s3")
public record S3Properties(String bucket, String endpoint) {}
