package com.lgcns.infra.dynamodb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cloud.aws.dynamodb")
public record DynamoDbProperties(String tableName) {}
