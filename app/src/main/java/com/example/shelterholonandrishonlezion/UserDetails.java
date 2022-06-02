package com.example.shelterholonandrishonlezion;

import android.util.Log;

public class UserDetails {
    private String mail;
    private String password;
    private String name;
    private String type;

    public UserDetails(){}

    public UserDetails(String mail, String password, String name, String type) {
        this.mail = mail;
        this.password = password;
        this.name = name;
        this.type = type;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
