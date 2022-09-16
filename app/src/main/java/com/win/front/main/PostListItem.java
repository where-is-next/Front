package com.win.front.main;

public class PostListItem {
    String title;
    String nickname;
    String date;
    String contents;
    String imageURI;

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "PostListItem{" +
                "title='" + title + '\'' +
                ", nickname='" + nickname + '\'' +
                ", date='" + date + '\'' +
                ", contents='" + contents + '\'' +
                ", imageURI='" + imageURI + '\'' +
                '}';
    }
}
