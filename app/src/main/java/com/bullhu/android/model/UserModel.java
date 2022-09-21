package com.bullhu.android.model;

import me.pushy.sdk.lib.jackson.annotation.ObjectIdGenerators;

public class UserModel {

    String id = "";
    String name = "";
    String email = "";
    String phone_country_code = "";
    String phone = "";
    String photo = "";
    int user_type = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPhone_country_code() {
        return phone_country_code;
    }

    public void setPhone_country_code(String phone_country_code) {
        this.phone_country_code = phone_country_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }
}
