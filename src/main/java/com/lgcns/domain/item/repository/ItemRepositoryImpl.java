package com.lgcns.domain.item.repository;

import static com.lgcns.domain.item.domain.QItem.item;

import com.lgcns.domain.item.client.dto.ItemInfoResponse;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    @Override
    public List<ItemLocationResponse> findItemsWithSplitLocation(Long popupId) {
        StringExpression firstLetter =
                Expressions.stringTemplate("SUBSTRING({0}, 1, 1)", item.location);
        StringExpression lastLetter =
                Expressions.stringTemplate("SUBSTRING({0}, 2)", item.location);

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

    private BooleanExpression checkItemSearchName(String keyword) {
        return StringUtils.hasText(keyword) ? item.name.contains(keyword) : null;
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
}
