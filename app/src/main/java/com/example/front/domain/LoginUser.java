package com.example.front.domain;

import com.example.front.enumpack.UserEnum;

import java.io.Serializable;

public class LoginUser implements Serializable {
    private String id;
    private String pw;
    private String phone;
    private String nickname;
    private UserEnum userEnum;

    public LoginUser(String id, String pw, String phone, String nickname, UserEnum userEnum) {
        this.id = id;
        this.pw = pw;
        this.phone = phone;
        this.nickname = nickname;
        this.userEnum = userEnum;
    }

    @Override
    public String toString() {
        return "LoginUser{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userEnum=" + userEnum +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public UserEnum getUserEnum() {
        return userEnum;
    }
}
