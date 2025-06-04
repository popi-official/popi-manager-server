package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.domain.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Query("select i from Item i join fetch i.popup p join fetch p.manager where i.id = :id")
    List<Item> findByPopupId(Long popupId);

    @Modifying
    @Query(
            value =
                    """
    UPDATE item i
    JOIN popup p ON i.popup_id = p.popup_id
    SET i.average_sales = FLOOR(i.sales / (DATEDIFF(CURDATE(), p.popup_start_date) + 1))
    WHERE CURDATE() BETWEEN p.popup_start_date AND DATE_SUB(p.popup_end_date, INTERVAL 1 DAY)
    """,
            nativeQuery = true)
    void bulkUpdateAverageSalesForOperatingPopups();
}
