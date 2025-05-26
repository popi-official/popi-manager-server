package com.lgcns.domain.popup.dto.response;

public record PopupDetailsResponse(
        Long popupId,
        String popupName,
        String imageUrl,
        String popupOpenDate,
        String popupCloseDate,
        String reservationOpenDateTime,
        String reservationCloseDateTime,
        String address,
        String runOpenTime,
        String runCloseTime,
        Double latitude,
        Double longitude) {
    public static PopupDetailsResponse of(
            Long popupId,
            String popupName,
            String imageUrl,
            String popupOpenDate,
            String popupCloseDate,
            String reservationOpenDateTime,
            String reservationCloseDateTime,
            String address,
            String runOpenTime,
            String runCloseTime,
            Double latitude,
            Double longitude) {
        return new PopupDetailsResponse(
                popupId,
                popupName,
                imageUrl,
                popupOpenDate,
                popupCloseDate,
                reservationOpenDateTime,
                reservationCloseDateTime,
                address,
                runOpenTime,
                runCloseTime,
                latitude,
                longitude);
    }
}
