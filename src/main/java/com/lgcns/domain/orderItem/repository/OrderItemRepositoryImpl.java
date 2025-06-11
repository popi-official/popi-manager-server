package com.lgcns.domain.orderItem.repository;

import static com.lgcns.domain.item.domain.QItem.item;
import static com.lgcns.domain.orderItem.domian.QOrderItem.orderItem;
import static com.lgcns.domain.popup.domain.QPopup.popup;

import com.lgcns.domain.orderItem.dto.response.OrderItemResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<OrderItemResponse> findOrderItemsByPopupIdWithPagination(
            Long popupId, Long lastOrderItemId, int size) {
        List<OrderItemResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        OrderItemResponse.class,
                                        orderItem.id,
                                        orderItem.item.name,
                                        orderItem.item.recommendCount,
                                        orderItem.realCount,
                                        orderItem.item.lastRestockDateTime,
                                        orderItem.status))
                        .from(orderItem)
                        .join(orderItem.item, item)
                        .join(item.popup, popup)
                        .where(popup.id.eq(popupId), lastOrderItemCondition(lastOrderItemId))
                        .orderBy(orderItem.id.desc())
                        .limit(size + 1L)
                        .fetch();
        return checkLastPage(size, results);
    }

    private BooleanExpression lastOrderItemCondition(Long lastOrderItemId) {
        return (lastOrderItemId != null) ? orderItem.id.lt(lastOrderItemId) : null;
    }

    private <T> Slice<T> checkLastPage(int pageSize, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }
}
