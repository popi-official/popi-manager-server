package com.lgcns.domain.auth.repository;

import com.lgcns.domain.auth.domain.Manager;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    boolean existsByUsername(String username);

    Optional<Manager> findByUsername(String username);
}
