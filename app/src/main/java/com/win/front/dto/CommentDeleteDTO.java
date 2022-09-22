package com.win.front.dto;

public class CommentDeleteDTO {
    Long number;
    String id;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CommentDeleteDTO{" +
                "number=" + number +
                ", id='" + id + '\'' +
                '}';
    }
}
