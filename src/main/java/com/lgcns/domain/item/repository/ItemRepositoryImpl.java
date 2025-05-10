package com.lgcns.domain.item.repository;

import static com.lgcns.domain.item.domain.QItem.item;

import com.lgcns.domain.item.dto.response.ItemLocationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
