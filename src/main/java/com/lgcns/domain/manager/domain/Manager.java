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

    private String username;

    private String password;

    private Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Manager createManager(String username, String password) {
        return new Manager(username, password);
    }
}
