package com.lgcns.domain.reservationStats.client;

import com.lgcns.domain.reservationStats.client.dto.DailyMemberReservationCountResponse;
import com.lgcns.global.config.feign.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${reservation.service.name}",
        url = "${reservation.service.url:}",
        configuration = FeignConfig.class)
public interface ReservationServiceClient {

    @GetMapping("/internal/{popupId}/daily-count")
    DailyMemberReservationCountResponse findDailyMemberReservationCount(
            @PathVariable(name = "popupId") Long popupId);
}
