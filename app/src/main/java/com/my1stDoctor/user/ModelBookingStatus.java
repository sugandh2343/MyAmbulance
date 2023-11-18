package com.my1stDoctor.user;

public class ModelBookingStatus {
    String ambulance_no, driver_mobile,driver_profile,driver_uid,status;

    public ModelBookingStatus() {
    }

    public String getAmbulance_no() {
        return ambulance_no;
    }

    public void setAmbulance_no(String ambulance_no) {
        this.ambulance_no = ambulance_no;
    }

    public String getDriver_mobile() {
        return driver_mobile;
    }

    public void setDriver_mobile(String driver_mobile) {
        this.driver_mobile = driver_mobile;
    }

    public String getDriver_profile() {
        return driver_profile;
    }

    public void setDriver_profile(String driver_profile) {
        this.driver_profile = driver_profile;
    }

    public String getDriver_uid() {
        return driver_uid;
    }

    public void setDriver_uid(String driver_uid) {
        this.driver_uid = driver_uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ModelBookingStatus(String ambulance_no, String driver_mobile, String driver_profile, String driver_uid, String status) {
        this.ambulance_no = ambulance_no;
        this.driver_mobile = driver_mobile;
        this.driver_profile = driver_profile;
        this.driver_uid = driver_uid;
        this.status = status;
    }
}
