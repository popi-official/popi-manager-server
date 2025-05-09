package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.domain.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Item> findAllItemsByPopupId(Long popupId) {
        QItem item = QItem.item;

        return queryFactory.selectFrom(item).where(item.popup.id.eq(popupId)).fetch();
    }
}
