package com.example.capstoneproject.models;

import java.io.Serializable;

public class ReportModel implements Serializable {
    String reportId, userUid, date, time;
    public int Gmelina, Balinghoy, Ipil_ipil, Hagunoy, Talahib, Monggo, Patani;

    public ReportModel(String reportId, String userUid, String date, String time, int gmelina, int balinghoy, int ipil_ipil, int hagunoy, int talahib, int monggo, int patani) {
        this.reportId = reportId;
        this.userUid = userUid;
        this.date = date;
        this.time = time;
        this.Gmelina = gmelina;
        this.Balinghoy = balinghoy;
        this.Ipil_ipil = ipil_ipil;
        this.Hagunoy = hagunoy;
        this.Talahib = talahib;
        this.Monggo = monggo;
        this.Patani = patani;
    }

    public ReportModel() {
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getGmelina() {
        return Gmelina;
    }

    public void setGmelina(int gmelina) {
        Gmelina = gmelina;
    }

    public int getBalinghoy() {
        return Balinghoy;
    }

    public void setBalinghoy(int balinghoy) {
        Balinghoy = balinghoy;
    }

    public int getIpil_ipil() {
        return Ipil_ipil;
    }

    public void setIpil_ipil(int ipil_ipil) {
        Ipil_ipil = ipil_ipil;
    }

    public int getHagunoy() {
        return Hagunoy;
    }

    public void setHagunoy(int hagunoy) {
        Hagunoy = hagunoy;
    }

    public int getTalahib() {
        return Talahib;
    }

    public void setTalahib(int talahib) {
        Talahib = talahib;
    }

    public int getMonggo() {
        return Monggo;
    }

    public void setMonggo(int monggo) {
        Monggo = monggo;
    }

    public int getPatani() {
        return Patani;
    }

    public void setPatani(int patani) {
        Patani = patani;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "reportId='" + reportId + '\'' +
                ", userUid='" + userUid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", Gmelina=" + Gmelina +
                ", Balinghoy=" + Balinghoy +
                ", Ipil_ipil=" + Ipil_ipil +
                ", Hagunoy=" + Hagunoy +
                ", Talahib=" + Talahib +
                ", Monggo=" + Monggo +
                ", Patani=" + Patani +
                '}';
    }
    public void combineVal(int gmelina, int balinghoy, int ipil, int hagunoy, int talahib, int monggo, int patani) {
//        int gmelina_init = 0;
//        int balinghoy_init = 0;
//        int ipil_init = 0;
//        int hagunoy_init = 0;
//        int talahib_init = 0;
//        int monggo_init = 0;
//        int patani_init = 0;
//
//        gmelina_init += gmelina;
//        balinghoy_init += balinghoy;
//        ipil_init += ipil;
//        hagunoy_init += hagunoy;
//        talahib_init += talahib;
//        monggo_init += monggo;
//        patani_init += patani;

        this.Gmelina += gmelina;
        this.Balinghoy += balinghoy;
        this.Ipil_ipil += ipil;
        this.Hagunoy += hagunoy;
        this.Talahib += talahib;
        this.Monggo += monggo;
        this.Patani += patani;
    }
}
