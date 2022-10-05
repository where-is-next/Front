package com.win.front.domain;

public class Stamp {
    Long stamp;
    String id;
    String stampLocation;

    public Long getStamp() {
        return stamp;
    }

    public void setStamp(Long stamp) {
        this.stamp = stamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStampLocation() {
        return stampLocation;
    }

    public void setStampLocation(String stampLocation) {
        this.stampLocation = stampLocation;
    }

    @Override
    public String toString() {
        return "Stamp{" +
                "id='" + id + '\'' +
                ", stampLocation='" + stampLocation + '\'' +
                '}';
    }
}
