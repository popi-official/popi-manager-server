package com.lgcns.domain.popup.repository;

import static com.lgcns.domain.popup.domain.QPopup.popup;

import com.lgcns.domain.popup.dto.response.PopupInfoResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopupRepositoryImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PopupPreviewResponse> findAllPopupsByManagerId(Long managerId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PopupPreviewResponse.class, popup.id, popup.name, popup.imageUrl))
                .from(popup)
                .where(popup.manager.id.eq(managerId))
                .orderBy(popup.id.asc())
                .fetch();
    }

    @Override
    public Slice<PopupInfoResponse> findAllActivePopups(Long lastPopupId, int size) {
        List<PopupInfoResponse> results =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        PopupInfoResponse.class,
                                        popup.id,
                                        popup.name,
                                        popup.imageUrl,
                                        popup.popupStartDate.stringValue(),
                                        popup.popupEndDate.stringValue(),
                                        popup.address
                                                .roadAddress
                                                .concat(", ")
                                                .concat(popup.address.detailAddress)))
                        .from(popup)
                        .where(popup.popupEndDate.goe(LocalDate.now()), lastPopupId(lastPopupId))
                        .orderBy(popup.createdAt.desc())
                        .limit(size + 1L)
                        .fetch();

        return checkLastPage(size, results);
    }

    private BooleanExpression lastPopupId(Long popupId) {
        if (popupId == null) {
            return null;
        }
        return popup.id.gt(popupId);
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
