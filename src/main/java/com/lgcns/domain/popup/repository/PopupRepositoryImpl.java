package com.lgcns.domain.popup.repository;

import static com.lgcns.domain.popup.domain.QPopup.popup;

import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class PopupRepositoryImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PopupPreviewResponse.class, popup.id, popup.name, popup.imageUrl))
                .from(popup)
                .where(popup.manager.id.eq(managerId))
                .orderBy(popup.id.desc())
                .fetch();
    }
}
