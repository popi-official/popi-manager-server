package com.lgcns.domain.conversionStats.repository;

import static com.lgcns.domain.conversionStats.domain.QConversionStats.conversionStats;
import static com.lgcns.domain.item.domain.QItem.item;

import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import com.lgcns.domain.conversionStats.dto.response.ConversionStatsResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConversionStatsRepositoryImpl implements ConversionStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public ConversionStatsResponse findTopAndLowConversionItems(Long popupId) {
        List<ConversionItem> low = fetchSortedItems(popupId, conversionStats.conversionRate.asc());

        List<ConversionItem> high =
                fetchSortedItems(popupId, conversionStats.conversionRate.desc());

        return new ConversionStatsResponse(low, high);
    }

    private List<ConversionItem> fetchSortedItems(Long popupId, OrderSpecifier<?> order) {
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
                .where(conversionStats.popupId.eq(popupId))
                .orderBy(order)
                .limit(6)
                .fetch();
    }
}
