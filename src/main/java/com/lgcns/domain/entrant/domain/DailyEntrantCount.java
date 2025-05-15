package com.lgcns.domain.entrant.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyEntrantCount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_entrant_count_id")
    private Long id;

    private Long popupId;

    private Long entrantCount;

    @Builder
    private DailyEntrantCount(Long popupId, Long entrantCount) {
        this.popupId = popupId;
        this.entrantCount = entrantCount;
    }

    public static DailyEntrantCount createDailyEntrantCount(Long popupId, Long entrantCount) {
        return DailyEntrantCount.builder().popupId(popupId).entrantCount(entrantCount).build();
    }
}
