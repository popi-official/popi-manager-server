package com.lgcns.domain.visitorStats.dto.response;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderType {
    MALE("남성") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.maleCount();
        }
    },
    FEMALE("여성") {
        @Override
        public int extractCount(VisitorStatsResponse stats) {
            return stats.femaleCount();
        }
    };

    private final String gender;

    public abstract int extractCount(VisitorStatsResponse stats);

    @JsonValue
    public String toJson() {
        return gender;
    }
}
