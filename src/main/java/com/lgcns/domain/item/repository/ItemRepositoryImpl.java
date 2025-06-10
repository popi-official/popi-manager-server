package com.lgcns.domain.item.repository;

import static com.lgcns.domain.item.domain.QItem.item;
import static com.querydsl.core.types.dsl.Expressions.*;

import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;
    private static final int CHUNK_SIZE = 500;

    @Override
    public List<ItemLocationResponse> findItemsWithSplitLocation(Long popupId) {
        StringExpression firstLetter = stringTemplate("SUBSTRING({0}, 1, 1)", item.location);
        StringExpression lastLetter = stringTemplate("SUBSTRING({0}, 2)", item.location);

        return queryFactory
                .select(
                        Projections.constructor(
                                ItemLocationResponse.class,
                                item.id,
                                item.name,
                                item.imageUrl,
                                item.price,
                                item.stock,
                                item.minStock,
                                firstLetter,
                                lastLetter))
                .from(item)
                .where(item.popup.id.eq(popupId))
                .fetch();
    }

    @Override
    public Slice<ItemInfoResponse> findItemsByNameWithPagination(
            Long popupId, String keyword, Long lastItemId, int size) {
        List<ItemInfoResponse> responses =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ItemInfoResponse.class,
                                        item.id,
                                        item.name,
                                        item.imageUrl,
                                        item.price))
                        .from(item)
                        .where(
                                checkItemSearchName(keyword),
                                item.popup.id.eq(popupId),
                                lastItemCondition(lastItemId))
                        .orderBy(item.id.desc())
                        .limit(size + 1L)
                        .fetch();

        return checkLastPage(size, responses);
    }

    @Override
    public List<ItemInfoResponse> findRandomItems(Long popupId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ItemInfoResponse.class,
                                item.id,
                                item.name,
                                item.imageUrl,
                                item.price))
                .from(item)
                .where(item.popup.id.eq(popupId))
                .orderBy(numberTemplate(Double.class, "RAND()").asc())
                .limit(4)
                .fetch();
    }

    @Override
    public List<ItemForPaymentResponse> findItemsForPayment(Long popupId, List<Long> itemIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ItemForPaymentResponse.class,
                                item.id,
                                item.name,
                                item.price,
                                item.stock))
                .from(item)
                .where(item.popup.id.eq(popupId), item.id.in(itemIds))
                .fetch();
    }

    @Override
    public List<Item> findTopItemsByPopupId(Long popupId, int limit) {
        NumberExpression<Integer> totalScore = item.popularityScore.add(item.sales);

        return queryFactory
                .selectFrom(item)
                .where(item.popup.id.eq(popupId))
                .orderBy(totalScore.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Item> findAllByPopupId(Long popupId) {
        return queryFactory.selectFrom(item).where(item.popup.id.eq(popupId)).fetch();
    }

    @Override
    public void bulkUpdate(List<Item> itemList) {
        if (itemList.isEmpty()) return;

        for (int i = 0; i < itemList.size(); i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, itemList.size());
            List<Item> chunk = itemList.subList(i, end);

            bulkUpdateChunk(chunk);
        }
    }

    private BooleanExpression checkItemSearchName(String searchName) {
        return StringUtils.hasText(searchName) ? item.name.contains(searchName) : null;
    }

    private BooleanExpression lastItemCondition(Long itemId) {
        return (itemId != null) ? item.id.lt(itemId) : null;
    }

    private Slice<ItemInfoResponse> checkLastPage(int pageSize, List<ItemInfoResponse> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }

    private void bulkUpdateChunk(List<Item> chunk) {
        if (chunk.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE item SET ").append("popularity_score = CASE item_id ");

        for (Item item : chunk) {
            sb.append("WHEN ")
                    .append(item.getId())
                    .append(" THEN ")
                    .append(item.getPopularityScore())
                    .append(" ");
        }

        sb.append("END, sales = CASE item_id ");

        for (Item item : chunk) {
            sb.append("WHEN ")
                    .append(item.getId())
                    .append(" THEN ")
                    .append(item.getSales())
                    .append(" ");
        }

        sb.append("END, updated_at = NOW() WHERE item_id IN (");

        for (int i = 0; i < chunk.size(); i++) {
            sb.append(chunk.get(i).getId());
            if (i < chunk.size() - 1) sb.append(", ");
        }

        sb.append(")");

        entityManager.createNativeQuery(sb.toString()).executeUpdate();
    }
}
