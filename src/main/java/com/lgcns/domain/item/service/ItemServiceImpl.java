package com.lgcns.domain.item.service;

import com.lgcns.domain.item.domain.Item;
import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import com.lgcns.domain.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public void createItem(ItemCreateRequest request) {

        Item item =
                Item.createItem(
                        request.name(),
                        request.price(),
                        request.imageUrl(),
                        request.qty(),
                        request.minQty(),
                        request.location());

        itemRepository.save(item);
    }
}
