package com.example.chatapp.Model;

/**
 * A singular Vet Model
 * @author Aleem
 * @version 1.0
 * @since 2.0
 */
public class Vet {
    private String name;
    private String type;
    private String address;
    private String postal_code;
    private String tel_office_1;
    private String tel_office_2;
    private String fax_office;
    private double latitude;
    private double longtitude;

    public Vet() {}

    public Vet(String name, String type, String address, String postal_code, String tel_office_1, String tel_office_2, String fax_office, double latitude, double longtitude) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.postal_code = postal_code;
        this.tel_office_1 = tel_office_1;
        this.tel_office_2 = tel_office_2;
        this.fax_office = fax_office;
        this.latitude = latitude;
        this.longtitude = longtitude;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getTel_office_1() {
        return tel_office_1;
    }

    public void setTel_office_1(String tel_office_1) {
        this.tel_office_1 = tel_office_1;
    }

    public String getTel_office_2() {
        return tel_office_2;
    }

    public void setTel_office_2(String tel_office_2) {
        this.tel_office_2 = tel_office_2;
    }

    public String getFax_office() {
        return fax_office;
    }

    public void setFax_office(String fax_office) {
        this.fax_office = fax_office;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
