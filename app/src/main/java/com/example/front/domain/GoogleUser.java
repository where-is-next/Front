package com.example.front.domain;

import com.google.gson.annotations.SerializedName;

public class GoogleUser {

    private String name;
    private String email;

    public GoogleUser(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
