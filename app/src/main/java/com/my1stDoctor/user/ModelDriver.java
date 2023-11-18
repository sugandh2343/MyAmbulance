package com.my1stDoctor.user;

public class ModelDriver {
    String uid,email,name,phone,address,latitude,longitude,online,accountType, image_profile, image_aadharFront,
            image_aadharBack,image_dl,image_rc,image_fitness,image_pollution,image_insurance,timestamp,token,dlNo,aadharNo, amulanceNo;
    Double distance;

    public ModelDriver() {
    }

    public ModelDriver(String uid, String email, String name, String phone, String address, String latitude, String longitude, String online,
                       String accountType, String image_profile, String image_aadharFront, String image_aadharBack, String image_dl,
                       String image_rc, String image_fitness, String image_pollution, String image_insurance, String timestamp,
                       String token, String dlNo, String aadharNo, String amulanceNo, Double distance) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.online = online;
        this.accountType = accountType;
        this.image_profile = image_profile;
        this.image_aadharFront = image_aadharFront;
        this.image_aadharBack = image_aadharBack;
        this.image_dl = image_dl;
        this.image_rc = image_rc;
        this.image_fitness = image_fitness;
        this.image_pollution = image_pollution;
        this.image_insurance = image_insurance;
        this.timestamp = timestamp;
        this.token = token;
        this.dlNo = dlNo;
        this.aadharNo = aadharNo;
        this.amulanceNo = amulanceNo;
        this.distance = distance;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getImage_profile() {
        return image_profile;
    }

    public void setImage_profile(String image_profile) {
        this.image_profile = image_profile;
    }

    public String getImage_aadharFront() {
        return image_aadharFront;
    }

    public void setImage_aadharFront(String image_aadharFront) {
        this.image_aadharFront = image_aadharFront;
    }

    public String getImage_aadharBack() {
        return image_aadharBack;
    }

    public void setImage_aadharBack(String image_aadharBack) {
        this.image_aadharBack = image_aadharBack;
    }

    public String getImage_dl() {
        return image_dl;
    }

    public void setImage_dl(String image_dl) {
        this.image_dl = image_dl;
    }

    public String getImage_rc() {
        return image_rc;
    }

    public void setImage_rc(String image_rc) {
        this.image_rc = image_rc;
    }

    public String getImage_fitness() {
        return image_fitness;
    }

    public void setImage_fitness(String image_fitness) {
        this.image_fitness = image_fitness;
    }

    public String getImage_pollution() {
        return image_pollution;
    }

    public void setImage_pollution(String image_pollution) {
        this.image_pollution = image_pollution;
    }

    public String getImage_insurance() {
        return image_insurance;
    }

    public void setImage_insurance(String image_insurance) {
        this.image_insurance = image_insurance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDlNo() {
        return dlNo;
    }

    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getAmulanceNo() {
        return amulanceNo;
    }

    public void setAmulanceNo(String amulanceNo) {
        this.amulanceNo = amulanceNo;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
