package com.lgcns.domain.popup.repository;

import static com.lgcns.domain.popup.domain.QPopup.popup;
import static com.lgcns.domain.survey.domain.QChoice.choice;
import static com.lgcns.domain.survey.domain.QSurvey.survey;

import com.lgcns.domain.popup.dto.response.ChoiceInfoResponse;
import com.lgcns.domain.popup.dto.response.PopupDetailsResponse;
import com.lgcns.domain.popup.dto.response.PopupInfoResponse;
import com.lgcns.domain.popup.dto.response.PopupPreviewResponse;
import com.lgcns.domain.popup.exception.PopupErrorCode;
import com.lgcns.global.error.exception.CustomException;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    public Slice<PopupInfoResponse> findPopupsByNameWithPagination(
            String keyword, Long lastPopupId, int size) {
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
                                        getFullAddress()))
                        .from(popup)
                        .where(
                                popup.popupEndDate.goe(LocalDate.now()),
                                checkPopupSearchName(keyword),
                                lastPopupCondition(lastPopupId))
                        .orderBy(popup.createdAt.desc())
                        .limit(size + 1L)
                        .fetch();

        return checkLastPage(size, results);
    }

    @Override
    public List<ChoiceInfoResponse> findAllChoices(Long popupId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ChoiceInfoResponse.class, survey.id, choice.id, choice.content))
                .from(popup)
                .join(popup.surveyList, survey)
                .join(survey.choiceList, choice)
                .where(popup.id.eq(popupId))
                .orderBy(survey.id.asc(), choice.number.asc())
                .fetch();
    }

    @Override
    public List<PopupDetailsResponse> findReservedPopupInfo(List<Long> popupIds) {
        return queryFactory
                .select(
                        Projections.constructor(
                                PopupDetailsResponse.class,
                                popup.id,
                                popup.name,
                                popup.imageUrl,
                                popup.popupStartDate.stringValue(),
                                popup.popupEndDate.stringValue(),
                                popup.reservationOpenDateTime.stringValue(),
                                popup.reservationCloseDateTime.stringValue(),
                                getFullAddress(),
                                popup.runOpenTime.stringValue(),
                                popup.runCloseTime.stringValue(),
                                popup.address.latitude,
                                popup.address.longitude))
                .from(popup)
                .where(popup.id.in(popupIds))
                .fetch();
    }

    @Override
    public PopupDetailsResponse findPopupDetailsById(Long popupId) {
        PopupDetailsResponse result =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        PopupDetailsResponse.class,
                                        popup.id,
                                        popup.name,
                                        popup.imageUrl,
                                        popup.popupStartDate.stringValue(),
                                        popup.popupEndDate.stringValue(),
                                        popup.reservationOpenDateTime.stringValue(),
                                        popup.reservationCloseDateTime.stringValue(),
                                        getFullAddress(),
                                        popup.runOpenTime.stringValue(),
                                        popup.runCloseTime.stringValue(),
                                        popup.address.latitude,
                                        popup.address.longitude))
                        .from(popup)
                        .where(popup.id.eq(popupId))
                        .fetchOne();

        if (result == null) {
            throw new CustomException(PopupErrorCode.POPUP_NOT_FOUND);
        }

        return result;
    }

    @Override
    public List<PopupInfoResponse> findPopupsByIds(List<Long> popupIds, int limit) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                PopupInfoResponse.class,
                                popup.id,
                                popup.name,
                                popup.imageUrl,
                                popup.popupStartDate.stringValue(),
                                popup.popupEndDate.stringValue(),
                                getFullAddress()))
                .from(popup)
                .where(popup.id.in(popupIds))
                .orderBy(popup.id.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<PopupInfoResponse> findRandomPopups(List<Long> excludeIds, int size) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                PopupInfoResponse.class,
                                popup.id,
                                popup.name,
                                popup.imageUrl,
                                popup.popupStartDate.stringValue(),
                                popup.popupEndDate.stringValue(),
                                getFullAddress()))
                .from(popup)
                .where(popup.id.notIn(excludeIds))
                .orderBy(randomOrder())
                .limit(size)
                .fetch();
    }

    private OrderSpecifier<Double> randomOrder() {
        return Expressions.numberTemplate(Double.class, "function('rand')").asc();
    }

    private BooleanExpression checkPopupSearchName(String keyword) {
        return StringUtils.hasText(keyword) ? popup.name.contains(keyword.trim()) : null;
    }

    private BooleanExpression lastPopupCondition(Long popupId) {
        return (popupId != null) ? popup.id.gt(popupId) : null;
    }

    private <T> Slice<T> checkLastPage(int pageSize, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageSize) {
            hasNext = true;
            results.remove(pageSize);
        }

        return new SliceImpl<>(results, PageRequest.of(0, pageSize), hasNext);
    }

    private StringExpression getFullAddress() {
        return popup.address.roadAddress.concat(", ").concat(popup.address.detailAddress);
    }
}
