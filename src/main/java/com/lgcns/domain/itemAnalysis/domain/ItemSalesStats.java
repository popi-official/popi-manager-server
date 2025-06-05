package com.lgcns.domain.itemAnalysis.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemSalesStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_sales_stats_id")
    private Long id;

    private Long popupId;

    private Long itemId;

    private int salesVolume = 0;

    @Builder
    private ItemSalesStats(Long popupId, Long itemId, int salesVolume) {
        this.popupId = popupId;
        this.itemId = itemId;
        this.salesVolume = salesVolume;
    }

    public static ItemSalesStats createItemSalesStats(Long popupId, Long itemId, int salesVolume) {
        return ItemSalesStats.builder()
                .popupId(popupId)
                .itemId(itemId)
                .salesVolume(salesVolume)
                .build();
    }

    public void addSalesVolume(int quantity) {
        this.salesVolume += quantity;
    }
}
