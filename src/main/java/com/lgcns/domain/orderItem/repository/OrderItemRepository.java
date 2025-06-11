package com.lgcns.domain.orderItem.repository;

import com.lgcns.domain.orderItem.domian.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long>, OrderItemRepositoryCustom {

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.item i WHERE oi.id = :orderItemId")
    Optional<OrderItem> findById(Long orderItemId);
}
