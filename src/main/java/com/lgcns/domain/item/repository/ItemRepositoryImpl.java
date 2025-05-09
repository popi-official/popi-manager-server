package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.domain.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Item> findAllItemsByPopupId(Long popupId){
        QItem item = QItem.item;

        return queryFactory
                .selectFrom(item)
                .where(item.popup.id.eq(popupId))
                .fetch();
    }
}
