package com.lgcns.domain.image.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.lgcns.domain.image.dto.ImageFileExtension;
import com.lgcns.domain.image.dto.request.ImageUploadRequest;
import com.lgcns.domain.image.dto.response.PresignedUrlResponse;
import com.lgcns.domain.manager.domain.Manager;
import com.lgcns.global.util.ManagerUtil;
import com.lgcns.infra.s3.S3Properties;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ManagerUtil managerUtil;
    private final S3Properties s3Properties;
    private final AmazonS3 amazonS3;

    public PresignedUrlResponse createPresignedUrl(ImageUploadRequest request) {
        final Manager currentManager = managerUtil.getCurrentManager();

        String imageKey = generateUUID();
        String s3ObjectKey =
                createS3ObjectKey(
                        currentManager.getId(),
                        request.imageDirectory().getDirectory(),
                        imageKey,
                        request.imageFileExtension());
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                generatePresignedUrlRequest(
                        s3Properties.bucket(),
                        s3ObjectKey,
                        request.imageFileExtension().getExtension());

        String presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();

        return new PresignedUrlResponse(presignedUrl);
    }

    private GeneratePresignedUrlRequest generatePresignedUrlRequest(
            String bucket, String s3ObjectKey, String imageFileExtension) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, s3ObjectKey, HttpMethod.PUT)
                        .withKey(s3ObjectKey)
                        .withContentType("image/" + imageFileExtension)
                        .withExpiration(getPresignedUrlExpiration());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());

        return generatePresignedUrlRequest;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String createS3ObjectKey(
            Long managerId,
            String directory,
            String imageKey,
            ImageFileExtension imageFileExtension) {
        return managerId
                + "/"
                + directory
                + "/"
                + imageKey
                + "."
                + imageFileExtension.getExtension();
    }

    private Date getPresignedUrlExpiration() {
        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3);
        expiration.setTime(expTime);

        return expiration;
    }
}
