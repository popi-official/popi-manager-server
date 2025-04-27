package com.lgcns.domain.popup.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopupAddress {
    private String roadAddress;
    private String detailAddress;
    private Double latitude;
    private Double longitude;

    private PopupAddress(
            String roadAddress, String detailAddress, Double latitude, Double longitude) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static PopupAddress createPopupAddress(
            String roadAddress, String detailAddress, Double latitude, Double longitude) {
        return new PopupAddress(roadAddress, detailAddress, latitude, longitude);
    }
}
