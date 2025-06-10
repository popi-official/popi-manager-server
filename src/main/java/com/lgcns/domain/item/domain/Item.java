package com.lgcns.domain.item.domain;

import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.global.error.exception.CustomException;
import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private int popularityScore;
    private int sales;
    private int averageSales;

    private LocalDateTime lastRestockDateTime;

    private int recommendCount;
    private Boolean isAlarmed;

    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();

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
        this.popularityScore = 0;
        this.sales = 0;
        this.averageSales = 0;
        this.lastRestockDateTime = LocalDateTime.now();
        this.recommendCount = stock;
        this.isAlarmed = false;
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

    public void updateMinStock(int minStock) {
        this.minStock = minStock;
    }

    public void updatePopularityScore(int popularityScore) {
        this.popularityScore = popularityScore;
    }

    public void decreaseStockAndIncreaseSales(int quantity) {
        if (this.stock < quantity) {
            throw new CustomException(ItemErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
        this.sales += quantity;
    }

    public void updateIsAlarmed(Boolean isAlarmed) {
        this.isAlarmed = isAlarmed;
    }

    public Boolean checkOutOfStockAndAlarmed() {
        return this.stock <= this.minStock + this.averageSales && !this.isAlarmed;
    }
}
