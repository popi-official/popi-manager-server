package com.lgcns.domain.item.domain;

import com.lgcns.domain.popup.domain.Popup;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private String name;

    private String imageUrl;

    private int price;

    private int stock;

    private int minStock;

    private String location;

    @Builder(access = AccessLevel.PRIVATE)
    public Item(
            Popup popup,
            String name,
            String imageUrl,
            int price,
            int stock,
            int minStock,
            String location) {
        this.popup = popup;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.minStock = minStock;
        this.location = location;
    }

    public static Item createItem(
            Popup popup,
            String name,
            String imageUrl,
            int price,
            int stock,
            int minStock,
            String location) {
        return Item.builder()
                .popup(popup)
                .name(name)
                .imageUrl(imageUrl)
                .price(price)
                .stock(stock)
                .minStock(minStock)
                .location(location)
                .build();
    }
}
