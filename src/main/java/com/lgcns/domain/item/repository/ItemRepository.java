package com.lgcns.domain.item.repository;

import com.lgcns.domain.item.domain.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    @Query("select i from Item i join fetch i.popup p join fetch p.manager where i.id = :id")
    Optional<Item> findWithPopupAndManagerById(@Param("id") Long id);
}
