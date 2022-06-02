package com.example.shelterholonandrishonlezion;

import android.util.Log;

public class PlaceDetails {
    private String accessibility;
    private String address;
    private String area;
    private String city;
    private double column;
    private String count_peoples;
    private String id_comments;
    private double latitude;
    private double longitude;
    private String name;
    private int num_shelter;
    private long number;
    private String number_home;

    public PlaceDetails(){}

    public PlaceDetails(String accessibility, String address, String area, String city, double column, String count_peoples, String id_comments, double latitude, double longitude, String name, int num_shelter, long number, String number_home) {
        this.accessibility = accessibility;
        this.address = address;
        this.area = area;
        this.city = city;
        this.column = column;
        this.count_peoples = count_peoples;
        this.id_comments = id_comments;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.num_shelter = num_shelter;
        this.number = number;
        this.number_home = number_home;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getColumn() {
        return column;
    }

    public void setColumn(double column) {
        this.column = column;
    }

    public String getCount_peoples() {
        return count_peoples;
    }

    public void setCount_peoples(String count_peoples) {
        this.count_peoples = count_peoples;
    }

    public String getId_comments() {
        return id_comments;
    }

    public void setId_comments(String id_comments) {
        this.id_comments = id_comments;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum_shelter() {
        return num_shelter;
    }

    public void setNum_shelter(int num_shelter) {
        this.num_shelter = num_shelter;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getNumber_home() {
        return number_home;
    }

    public void setNumber_home(String number_home) {
        this.number_home = number_home;
    }
}
