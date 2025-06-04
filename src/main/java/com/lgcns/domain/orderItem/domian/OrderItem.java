package com.lgcns.domain.orderItem.domian;

import com.lgcns.domain.item.domain.Item;
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

    private int realCount;

    private OrderItemStatus status;

    @Builder
    private OrderItem(Item item, int realCount) {
        this.item = item;
        this.realCount = realCount;
        this.status = OrderItemStatus.PENDING;
    }

    public static OrderItem createOrderItem(Item item, int realCount) {
        return OrderItem.builder().item(item).realCount(realCount).build();
    }
}
