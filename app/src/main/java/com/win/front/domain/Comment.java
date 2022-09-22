package com.win.front.domain;

public class Comment {
    Long number;

    String number_post;
    String nickname;
    String contents;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getNumber_post() {
        return number_post;
    }

    public void setNumber_post(String number_post) {
        this.number_post = number_post;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "number=" + number +
                ", number_post='" + number_post + '\'' +
                ", nickname='" + nickname + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }
}
