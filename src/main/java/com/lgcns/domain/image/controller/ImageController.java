package com.lgcns.domain.image.controller;

import com.lgcns.domain.image.dto.request.ImageUploadRequest;
import com.lgcns.domain.image.dto.response.PresignedUrlResponse;
import com.lgcns.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "2. 이미지 API", description = "이미지 관련 API입니다.")
public class ImageController {

    private final ImageService imageService;

    @Operation(
            summary = "이미지 업로드용 Presigned URL 생성",
            description =
                    "디렉토리(popup/item)와 확장자(png/jpeg)에 따라 이미지를 업로드할 수 있는 Presigned URL을 생성합니다.")
    @PostMapping("/presigned-url")
    public PresignedUrlResponse presignedUrlResponse(
            @Valid @RequestBody ImageUploadRequest request) {
        return imageService.createPresignedUrl(request);
    }
}
