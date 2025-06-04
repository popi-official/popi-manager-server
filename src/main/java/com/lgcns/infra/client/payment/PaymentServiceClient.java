package com.lgcns.infra.client.payment;

import com.lgcns.domain.conversionStats.dto.response.ItemBuyerCountResponse;
import com.lgcns.domain.paymentStats.dto.response.AverageAmountResponse;
import com.lgcns.global.config.feign.FeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${payment.service.name}",
        url = "${payment.service.url:}",
        configuration = FeignConfig.class)
public interface PaymentServiceClient {
    @GetMapping("/internal/{popupId}/average-purchase")
    AverageAmountResponse findAverageAmount(@PathVariable(name = "popupId") Long popupId);

    @GetMapping("/internal/{popupId}/buyer-counts")
    List<ItemBuyerCountResponse> countItemBuyerByPopupId(@PathVariable Long popupId);
}
