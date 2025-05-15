package com.lgcns.domain.paymentStats.repository;

import com.lgcns.domain.paymentStats.domain.PaymentStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatsRepository
        extends JpaRepository<PaymentStats, Long>, PaymentStatsRepositoryCustom {}
