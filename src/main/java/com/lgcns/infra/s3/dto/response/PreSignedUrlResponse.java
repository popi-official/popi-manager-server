package com.lgcns.infra.s3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "S3 PreSigned URL DTO")
public record PreSignedUrlResponse(
        @Schema(
                        description = "preSigned URL",
                        example = "https://popitestbucket.s3.ap-northeast-2.amazonaws.com/popi/abc")
                String preSignedUrl) {
    public static PreSignedUrlResponse from(String preSignedUrl) {
        return new PreSignedUrlResponse(preSignedUrl);
    }
}
