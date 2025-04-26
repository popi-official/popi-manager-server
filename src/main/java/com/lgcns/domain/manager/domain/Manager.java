package com.lgcns.domain.manager.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private Long id;

    private String username;

    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Manager createManager(
            String username, String password, PasswordEncoder passwordEncoder) {
        return Manager.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();
    }
}
