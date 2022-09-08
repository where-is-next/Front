package com.win.front.dto;

public class ChangePwDTO {
    private String id;
    private String phoneNum;
    private String pw;

    public ChangePwDTO(String id, String phoneNum, String pw) {
        this.id = id;
        this.phoneNum = phoneNum;
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "ChangePwDTO{" +
                "id='" + id + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", pw='" + pw + '\'' +
                '}';
    }
}
