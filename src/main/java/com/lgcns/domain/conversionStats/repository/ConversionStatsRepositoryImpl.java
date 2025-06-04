package com.lgcns.domain.conversionStats.repository;

import static com.lgcns.domain.conversionStats.domain.QConversionStats.conversionStats;
import static com.lgcns.domain.item.domain.QItem.item;

import com.lgcns.domain.conversionStats.dto.response.ConversionItem;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConversionStatsRepositoryImpl implements ConversionStatsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConversionItem> findTop6LowConversionItemsByPopupId(Long popupId) {
        return fetchTop6ConversionItems(popupId, SortDirection.ASC);
    }

    @Override
    public List<ConversionItem> findTop6HighConversionItemsByPopupId(Long popupId) {
        return fetchTop6ConversionItems(popupId, SortDirection.DESC);
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
