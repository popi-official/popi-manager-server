package com.lgcns.domain.item.service;

import com.lgcns.domain.item.dto.request.ItemCreateRequest;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

public interface ItemService {

    void createItem(Long popupId, ItemCreateRequest request);

    void createItemByExcel(MultipartFile itemFile, Long popupId)
            throws InvalidFormatException, IOException;
}
