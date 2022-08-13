package com.example.front.domain;

import com.example.front.enumpack.UserEnum;

import java.io.Serializable;

public class SocialUser implements Serializable {

    private String id;  // 소셜 로그인의 id값 ( getId() ), LoginUser의 id
    private String pw;
    private String email;
    private String nickname; // nickname
    private UserEnum userEnum;

    public SocialUser(String id, String pw, String email, String nickname, UserEnum userEnum) {
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.nickname = nickname;
        this.userEnum = userEnum;
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", email='" + email + '\'' +
                ", name='" + nickname + '\'' +
                ", userEnum=" + userEnum +
                '}';
    }
}
