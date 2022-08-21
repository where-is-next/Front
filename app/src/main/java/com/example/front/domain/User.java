package com.example.front.domain;

public class User {
    public String id;
    public String pw;
    public String phone;
    public String nickname;
    public String userEnum;

    public User(String id, String pw, String phone, String nickname, String userEnum) {
        this.id = id;
        this.pw = pw;
        this.phone = phone;
        this.nickname = nickname;
        this.userEnum = userEnum;
    }
}
