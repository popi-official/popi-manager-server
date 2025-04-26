package com.lgcns.domain.manager.repository;

import com.lgcns.domain.manager.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long>, ManagerRepositoryCustom {
    boolean existsByUsername(String username);
}
