package com.lgcns.domain.itemAnalysis.repository;

import static com.lgcns.domain.item.domain.QItem.item;
import static com.lgcns.domain.itemAnalysis.domain.QItemAnalysis.itemAnalysis;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemAnalysisRepositoryCustomImpl implements ItemAnalysisRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ItemAnalysis> findTop3ItemsByPopupId(Long popupId) {
        NumberExpression<Integer> totalScore =
                itemAnalysis.popularityScore.add(itemAnalysis.salesVolume);

        return queryFactory
                .selectFrom(itemAnalysis)
                .join(itemAnalysis.item, item)
                .fetchJoin()
                .where(item.popup.id.eq(popupId))
                .orderBy(totalScore.desc())
                .limit(3)
                .fetch();
    }
}
