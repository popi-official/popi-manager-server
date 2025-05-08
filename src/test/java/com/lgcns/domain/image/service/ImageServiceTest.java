package com.lgcns.domain.image.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.lgcns.IntegrationTest;
import com.lgcns.domain.image.dto.ImageDirectory;
import com.lgcns.domain.image.dto.ImageFileExtension;
import com.lgcns.domain.image.dto.request.ImageUploadRequest;
import com.lgcns.domain.image.dto.response.PresignedUrlResponse;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.domain.manager.repository.ManagerRepository;
import com.lgcns.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class ImageServiceTest extends IntegrationTest {
    @Autowired private ImageService imageService;
    @Autowired private ManagerRepository managerRepository;

    private Manager manager;

    @BeforeEach
    void setUp() {
        manager = managerRepository.save(Manager.createManager("testUsername", "testPassword"));

        UserDetails userDetails = new PrincipalDetails(manager.getId(), manager.getRole(), null);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Nested
    class 이미지_업로드용_Presigned_URL을_생성할_때 {
        @Test
        void 입력_값이_정상이라면_팝업_이미지_업로드용_Presigned_URL이_정상적으로_생성된다() {
            // given
            ImageUploadRequest request =
                    new ImageUploadRequest(ImageFileExtension.JPEG, ImageDirectory.POPUP);

            PresignedUrlResponse response = imageService.createPresignedUrl(request);

            // when & then
            assertThat(response.presignedUrl())
                    .containsPattern(
                            String.format(
                                    "/%s/%s/[\\w\\-]+\\.jpeg",
                                    manager.getId(), request.imageDirectory().getDirectory()));
        }

        @Test
        void 입력_값이_정상이라면_상품_이미지_업로드용_Presigned_URL이_정상적으로_생성된다() {
            // given
            ImageUploadRequest request =
                    new ImageUploadRequest(ImageFileExtension.JPEG, ImageDirectory.ITEM);

            PresignedUrlResponse response = imageService.createPresignedUrl(request);

            // when & then
            assertThat(response.presignedUrl())
                    .containsPattern(
                            String.format(
                                    "/%s/%s/[\\w\\-]+\\.jpeg",
                                    manager.getId(), request.imageDirectory().getDirectory()));
        }
    }
}
