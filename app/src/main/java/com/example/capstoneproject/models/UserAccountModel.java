package com.example.capstoneproject.models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserAccountModel implements Serializable {
    public String userUid;
    public String firstname;
    public String middlename;
    public String lastname;
    public String sex;
    public String barangay;
    public String municipality;
    public String province;
    public String email;
    public String password;

    public String role;
    public int age;
    public String phoneNumber;
    public String profileImg;
    public ArrayList<String> reports;

    public UserAccountModel(String userUid, String firstname, String middlename, String lastname, String sex, int age, String phoneNumber, String barangay, String municipality, String province, String email, String password, String role, String profileImg, ArrayList<String> reports) {
        this.userUid = userUid;
        this.firstname = firstname;
        this.middlename = middlename;
        this.lastname = lastname;
        this.sex = sex;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.barangay = barangay;
        this.municipality = municipality;
        this.province = province;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profileImg = profileImg;
        this.reports = reports;
    }

    public UserAccountModel() {

    }

    @Override
    public String toString() {
        return "UserAccountData{" +
                "userUid='" + userUid + '\'' +
                ", firstname='" + firstname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", lastname='" + lastname + '\'' +
                ", sex='" + sex + '\'' +
                ", barangay='" + barangay + '\'' +
                ", municipality='" + municipality + '\'' +
                ", province='" + province + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", reports=" + reports +
                '}';
    }
}
