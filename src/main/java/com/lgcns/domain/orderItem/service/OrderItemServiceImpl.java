package com.lgcns.domain.orderItem.service;

import static com.lgcns.domain.notification.domain.Popularity.hot;
import static com.lgcns.domain.notification.domain.Popularity.normal;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.exception.ItemErrorCode;
import com.lgcns.domain.item.repository.ItemRepository;
import com.lgcns.domain.itemAnalysis.domain.ItemAnalysis;
import com.lgcns.domain.itemAnalysis.repository.ItemAnalysisRepository;
import com.lgcns.domain.notification.domain.Notification;
import com.lgcns.domain.notification.repository.NotificationRepository;
import com.lgcns.domain.orderItem.domian.OrderItem;
import com.lgcns.domain.orderItem.repository.OrderItemRepository;
import com.lgcns.domain.popup.domain.Popup;
import com.lgcns.global.error.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ItemAnalysisRepository itemAnalysisRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void createOrderItem(Long itemId) {
        Item item = itemRepository.findItemWithPopupAndMember(itemId);
        if (item == null) throw new CustomException(ItemErrorCode.ITEM_NOT_FOUND);

        OrderItem orderItem = OrderItem.createOrderItem(item);
        orderItemRepository.save(orderItem);
        createNotification(item);
    }

    private void createNotification(Item item) {
        Popup popup = item.getPopup();
        List<ItemAnalysis> top3Items = itemAnalysisRepository.findTop3ItemsByPopupId(popup.getId());

        boolean isHot =
                top3Items.stream()
                        .anyMatch(analysis -> analysis.getItem().getId().equals(item.getId()));

        Notification notification =
                Notification.createNotification(
                        popup.getManager().getId(),
                        popup.getId(),
                        item.getId(),
                        item.getName(),
                        isHot ? hot : normal,
                        item.getMinStock() + item.getAverageSales());

        notificationRepository.save(notification);
    }
}
