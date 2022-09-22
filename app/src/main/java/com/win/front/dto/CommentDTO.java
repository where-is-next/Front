package com.win.front.dto;

public class CommentDTO {
    String post_number;
    String comment_nickname;
    String comment_contents;

    public String getPost_number() {
        return post_number;
    }

    public void setPost_number(String post_number) {
        this.post_number = post_number;
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
        return "CommentDTO{" +
                "post_number='" + post_number + '\'' +
                ", comment_nickname='" + comment_nickname + '\'' +
                ", comment_contents='" + comment_contents + '\'' +
                '}';
    }
}
