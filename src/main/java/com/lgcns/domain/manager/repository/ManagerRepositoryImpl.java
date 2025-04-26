package com.lgcns.domain.manager.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ManagerRepositoryImpl implements ManagerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
}
