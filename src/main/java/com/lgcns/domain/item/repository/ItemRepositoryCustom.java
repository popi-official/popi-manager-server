package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.domain.Item;
import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> findAllItemsByPopupId(Long popupId);
}
