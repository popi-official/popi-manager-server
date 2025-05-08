package com.lgcns.domain.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(@Schema(description = "Presigned URL") String presignedUrl) {}
