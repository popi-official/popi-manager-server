package com.lgcns.domain.item.repository;

import static com.lgcns.domain.item.domain.QItem.item;
import static com.lgcns.domain.manager.domain.QManager.manager;
import static com.querydsl.core.types.dsl.Expressions.*;

import com.lgcns.domain.item.client.dto.response.ItemForPaymentResponse;
import com.lgcns.domain.item.client.dto.response.ItemInfoResponse;
import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import com.lgcns.domain.popup.domain.QPopup;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Item findItemWithPopupAndMember(Long itemId) {
        return queryFactory
                .selectFrom(item)
                .join(item.popup, QPopup.popup)
                .fetchJoin()
                .join(item.popup.manager, manager)
                .fetchJoin()
                .where(item.id.eq(itemId))
                .fetchOne();
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

    @Override
    public List<Item> findItemsByPopupId(Long popupId) {
        return queryFactory.selectFrom(item).where(item.popup.id.eq(popupId)).fetch();
    }
}
