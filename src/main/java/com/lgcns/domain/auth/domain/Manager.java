package com.lgcns.domain.auth.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private ManagerRole managerRole;

    @Builder(access = AccessLevel.PRIVATE)
    private Manager(String username, String password) {
        this.username = username;
        this.password = password;
        this.managerRole = ManagerRole.USER;
    }

    public static Manager createManager(String username, String password) {
        return Manager.builder().username(username).password(password).build();
    }
}
