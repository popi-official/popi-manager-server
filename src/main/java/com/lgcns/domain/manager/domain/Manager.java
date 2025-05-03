package com.lgcns.domain.manager.domain;

import com.lgcns.global.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    private ManagerRole role;

    private Manager(String username, String password, ManagerRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public static Manager createManager(String username, String password) {
        return new Manager(username, password, ManagerRole.USER);
    }
}
