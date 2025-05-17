package com.lgcns.domain.notification.repository;

import static com.lgcns.domain.notification.domain.QNotification.notification;

import com.lgcns.domain.notification.dto.response.NotificationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NotificationResponse> findByManagerIdAndPopupId(Long managerId, Long popupId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                NotificationResponse.class,
                                notification.id,
                                notification.popularity,
                                notification.itemName,
                                notification.minStock,
                                notification.createdAt))
                .from(notification)
                .where(notification.managerId.eq(managerId), notification.popupId.eq(popupId))
                .fetch();
    }
}
