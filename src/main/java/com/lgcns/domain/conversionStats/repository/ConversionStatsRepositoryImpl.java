package com.lgcns.domain.conversionStats.repository;

import static com.lgcns.domain.conversionStats.domain.QConversionStats.conversionStats;
import static com.lgcns.domain.item.domain.QItem.item;

import com.lgcns.domain.conversionStats.domain.ConversionStats;
import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConversionStatsRepositoryImpl implements ConversionStatsRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConversionItem> findTop6LowConversionItemsByPopupId(Long popupId) {
        return fetchTop6ConversionItems(popupId, SortDirection.ASC);
    }

    @Override
    public List<ConversionItem> findTop6HighConversionItemsByPopupId(Long popupId) {
        return fetchTop6ConversionItems(popupId, SortDirection.DESC);
    }

    @Override
    public void bulkInsertConversionStats(List<ConversionStats> statsList) {
        if (statsList.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append(
                "INSERT INTO conversion_stats (popup_id, item_id, interested_count, buyer_count, conversion_rate, analyzed_date, analyzed_time) VALUES ");

        for (int i = 0; i < statsList.size(); i++) {
            ConversionStats cs = statsList.get(i);
            sb.append("(")
                    .append(cs.getPopupId())
                    .append(", ")
                    .append(cs.getItemId())
                    .append(", ")
                    .append("'")
                    .append(cs.getInterestedCount())
                    .append("', ")
                    .append("'")
                    .append(cs.getBuyerCount())
                    .append("', ")
                    .append("'")
                    .append(cs.getConversionRate())
                    .append("', ")
                    .append("'")
                    .append(cs.getAnalyzedDate())
                    .append("', ")
                    .append("'")
                    .append(cs.getAnalyzedTime())
                    .append("'")
                    .append(")");

            if (i < statsList.size() - 1) sb.append(", ");
        }

        em.createNativeQuery(sb.toString()).executeUpdate();
    }

    private List<ConversionItem> fetchTop6ConversionItems(Long popupId, SortDirection direction) {
        OrderSpecifier<Integer> orderSpecifier =
                (direction == SortDirection.DESC)
                        ? conversionStats.conversionRate.desc()
                        : conversionStats.conversionRate.asc();

        return queryFactory
                .select(
                        Projections.constructor(
                                ConversionItem.class,
                                item.name,
                                conversionStats.interestedCount,
                                conversionStats.buyerCount,
                                conversionStats.conversionRate))
                .from(conversionStats)
                .join(item)
                .on(item.id.eq(conversionStats.itemId))
                .where(
                        conversionStats.popupId.eq(popupId),
                        conversionStats.analyzedDate.eq(LocalDate.now()))
                .orderBy(orderSpecifier)
                .limit(6)
                .fetch();
    }

    private enum SortDirection {
        ASC,
        DESC
    }
}
