package com.lgcns.domain.image.controller;

import com.lgcns.domain.image.dto.request.ImageRequest;
import com.lgcns.domain.image.dto.response.PreSignedUrlResponse;
import com.lgcns.domain.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "7. Image API", description = "이미지 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned-url")
    @Operation(
            summary = "S3 preSigned url 생성",
            description = "파일 이름을 입력받아 S3 preSigned URL을 생성합니다.<br>" + "파일 이름과 확장자를 선택하세요.")
    public PreSignedUrlResponse preSignedUrlCreate(@RequestBody @Valid ImageRequest request) {
        return imageService.createPreSignedUrl("popi", request);
    }
}
