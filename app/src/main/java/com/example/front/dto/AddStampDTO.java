package com.example.front.dto;

public class AddStampDTO {
    String id;
    String stampLocation;

    public AddStampDTO(String id, String stampLocation) {
        this.id = id;
        this.stampLocation = stampLocation;
    }

    @Override
    public String toString() {
        return "AddStampDTO{" +
                "id='" + id + '\'' +
                ", stampLocation='" + stampLocation + '\'' +
                '}';
    }
}