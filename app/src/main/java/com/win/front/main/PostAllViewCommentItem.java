package com.win.front.main;

public class PostAllViewCommentItem {
    Long comment_number;
    String comment_post_number;
    String comment_nickname;
    String comment_contents;

    public Long getComment_number() {
        return comment_number;
    }

    public void setComment_number(Long comment_number) {
        this.comment_number = comment_number;
    }

    public String getComment_post_number() {
        return comment_post_number;
    }

    public void setComment_post_number(String comment_post_number) {
        this.comment_post_number = comment_post_number;
    }

    public String getComment_nickname() {
        return comment_nickname;
    }

    public void setComment_nickname(String comment_nickname) {
        this.comment_nickname = comment_nickname;
    }

    public String getComment_contents() {
        return comment_contents;
    }

    public void setComment_contents(String comment_contents) {
        this.comment_contents = comment_contents;
    }

    @Override
    public String toString() {
        return "PostAllViewCommentItem{" +
                "comment_number=" + comment_number +
                ", comment_post_number='" + comment_post_number + '\'' +
                ", comment_nickname='" + comment_nickname + '\'' +
                ", comment_contents='" + comment_contents + '\'' +
                '}';
    }
}
