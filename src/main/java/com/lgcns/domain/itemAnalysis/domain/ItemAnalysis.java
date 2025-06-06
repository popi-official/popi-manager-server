package com.lgcns.domain.itemAnalysis.domain;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemAnalysis extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_analysis_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int popularityScore;

    private double preSurveyPopularity;

    private int salesVolume;

    @Builder
    private ItemAnalysis(Item item) {
        this.item = item;
    }

    public static ItemAnalysis createItemAnalysis(Item item) {
        return ItemAnalysis.builder().item(item).build();
    }

    public void updateScores(int popularityScore, int salesVolume) {
        this.popularityScore = popularityScore;
        this.salesVolume = salesVolume;
    }
}
