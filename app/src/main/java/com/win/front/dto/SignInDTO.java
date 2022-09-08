package com.win.front.dto;

import com.win.front.enumpack.UserEnum;

import java.io.Serializable;

public class SignInDTO implements Serializable {
    private String id;
    private String pw;
    private UserEnum userEnum;

    public SignInDTO(String id, String pw, UserEnum userEnum) {
        this.id = id;
        this.pw = pw;
        this.userEnum = userEnum;
    }

    @Override
    public String toString() {
        return "SignInDTO{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", userEnum=" + userEnum +
                '}';
    }
}
