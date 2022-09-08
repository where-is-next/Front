package com.win.front.domain;

public class Location {
    private String name;
    private String latitude; // 위도
    private String longitude; // 경도
    private String address; // 주소
    private String url;     // 상세 페이지

    public Location(String name, String latitude, String longitude, String address, String url) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getUrl() {
        return url;
    }
}
