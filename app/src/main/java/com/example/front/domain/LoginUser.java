package com.example.front.domain;

import com.example.front.enumpack.UserEnum;

import java.io.Serializable;

public class LoginUser implements Serializable {
    private String id;
    private String pw;
    private String email;
    private String nickname;
    private UserEnum userEnum;

    public LoginUser(String id, String pw, String email, String nickname, UserEnum userEnum) {
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.nickname = nickname;
        this.userEnum = userEnum;
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userEnum=" + userEnum +
                '}';
    }
}
