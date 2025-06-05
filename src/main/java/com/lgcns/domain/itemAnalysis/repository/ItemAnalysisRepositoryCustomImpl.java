package com.lgcns.domain.itemAnalysis.repository;

import static com.lgcns.domain.item.domain.QItem.item;
import static com.lgcns.domain.itemAnalysis.domain.QItemAnalysis.itemAnalysis;

import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.dto.response.ItemTrendingResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemAnalysisRepositoryCustomImpl implements ItemAnalysisRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private static final int CHUNK_SIZE = 500;

    @Override
    public List<ItemTrendingResponse> findTopItemsByPopupId(Long popupId, int limit) {
        NumberExpression<Integer> totalScore =
                itemAnalysis.popularityScore.add(itemAnalysis.salesVolume);

        return queryFactory
                .select(
                        Projections.constructor(
                                ItemTrendingResponse.class,
                                item.id,
                                item.name,
                                item.imageUrl,
                                item.price,
                                item.stock))
                .from(itemAnalysis)
                .join(itemAnalysis.item, item)
                .where(item.popup.id.eq(popupId))
                .orderBy(totalScore.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<ItemAnalysis> findAllByPopupId(Long popupId) {
        return queryFactory
                .selectFrom(itemAnalysis)
                .join(itemAnalysis.item, item)
                .fetchJoin()
                .where(item.popup.id.eq(popupId))
                .fetch();
    }

    @Override
    public void bulkInsertOrUpdate(List<ItemAnalysis> itemAnalysisList) {
        if (itemAnalysisList.isEmpty()) return;

        for (int i = 0; i < itemAnalysisList.size(); i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, itemAnalysisList.size());
            List<ItemAnalysis> chunk = itemAnalysisList.subList(i, end);

            bulkInsertChunk(chunk);
        }
    }

    private void bulkInsertChunk(List<ItemAnalysis> chunk) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO item_analysis ")
                .append(
                        "(item_id, popularity_score, sales_volume, pre_survey_popularity, created_at, updated_at) VALUES ");

        for (int i = 0; i < chunk.size(); i++) {
            ItemAnalysis item = chunk.get(i);
            sb.append("(")
                    .append(item.getItem().getId())
                    .append(", ")
                    .append(item.getPopularityScore())
                    .append(", ")
                    .append(item.getSalesVolume())
                    .append(", ")
                    .append(item.getPreSurveyPopularity())
                    .append(", ")
                    .append("NOW(), NOW()")
                    .append(")");

            if (i < chunk.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(" ON DUPLICATE KEY UPDATE ")
                .append("popularity_score = VALUES(popularity_score), ")
                .append("sales_volume = VALUES(sales_volume), ")
                .append("updated_at = NOW()");

        entityManager.createNativeQuery(sb.toString()).executeUpdate();
    }
}
