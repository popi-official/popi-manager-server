package com.lgcns.domain.orderItem.repository;

import com.lgcns.domain.orderItem.domian.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
