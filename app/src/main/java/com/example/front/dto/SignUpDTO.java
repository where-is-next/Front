package com.example.front.dto;

public class SignUpDTO {
    private String id;
    private String phone;
    private String nickname;
    private String pw;

    public SignUpDTO(String id, String phone, String nickname, String pw) {
        this.id = id;
        this.phone = phone;
        this.nickname = nickname;
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "SignUpDTO{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
