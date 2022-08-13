package com.example.front.dto;

public class SignUpDTO {
    private String id;
    private String email;
    private String nickname;
    private String pw;

    public SignUpDTO(String id, String email, String nickname, String pw) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "SignUpDTO{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
