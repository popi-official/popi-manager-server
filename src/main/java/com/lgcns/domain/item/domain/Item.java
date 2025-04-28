package com.lgcns.domain.item.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private String price;

    private String imageUrl;

    private int qty;

    private int minQty;

    private String location;

    @Builder(access = AccessLevel.PRIVATE)
    public Item(String name, String price, String imageUrl, int qty, int minQty, String location) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.qty = qty;
        this.minQty = minQty;
        this.location = location;
    }

    public static Item createItem(
            String name, String price, String imageUrl, int qty, int minQty, String location) {
        return Item.builder()
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .qty(qty)
                .minQty(minQty)
                .location(location)
                .build();
    }
}
