package com.lgcns.domain.manager.domain;

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

    private String username;

    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private Manager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static Manager createManager(String username, String password) {
        return Manager.builder().username(username).password(password).build();
    }

    // signup - manager 엔티티 생성 후 DB 저장하는 과정
    // 1. api 를 통해서 {username, password} 데이터를 받음
    // 2. password를 암호화한다.
    // 3. 받아온 정보와 암호화된 password 를가지고 manager 엔티티를 만든다.
    // 4. 만든 엔티티를 DB에 저장한다.
    // *** spring security 설정
    // ** 암호화 방법 선정
    // * h2 DB 설정
    // * querydsl? 적용여부
    // *
}
