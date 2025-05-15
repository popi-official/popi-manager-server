package com.lgcns.domain.visitorStats.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgeGroupType {
    TEEN("10대") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.teenCount();
        }
    },
    TWENTY("20대") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.twentyCount();
        }
    },
    THIRTY("30대") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.thirtyCount();
        }
    },
    FORTY("40대") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.fortyCount();
        }
    };

    private final String ageGroup;

    public abstract int extractCount(VisitorStatsResponse stats);

    @JsonValue
    public String toJson() {
        return ageGroup;
    }
}
