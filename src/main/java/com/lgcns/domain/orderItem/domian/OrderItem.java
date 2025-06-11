package com.lgcns.domain.orderItem.domian;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer realCount;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    @Builder
    private OrderItem(Item item) {
        this.item = item;
        this.realCount = -1;
        this.status = OrderItemStatus.PENDING;
    }

    public static OrderItem createOrderItem(Item item) {
        return OrderItem.builder().item(item).build();
    }

    public void updateOrderItem(Integer realCount, OrderItemStatus status) {
        if (status == OrderItemStatus.COMPLETED && realCount < 0) {
            throw new CustomException(ItemErrorCode.INVALID_RESTOCK);
        }

        if (status == OrderItemStatus.COMPLETED) this.realCount = realCount;

        if (status == OrderItemStatus.CANCELED || status == OrderItemStatus.COMPLETED)
            this.status = status;
        item.updateIsAlarmed(false);
    }
}
