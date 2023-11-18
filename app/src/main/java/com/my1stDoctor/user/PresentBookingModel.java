package com.my1stDoctor.user;

public class PresentBookingModel {
    private String Duration,StartPointAddress,EndPointAddress,Fare,StartPoint_Lat,StartPoint_Lon,EndPoint_Lat,EndPoint_Lon,uid,
            passenger_name,passenger_mob,Distance,timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getPassenger_name() {
        return passenger_name;
    }

    public void setPassenger_name(String passenger_name) {
        this.passenger_name = passenger_name;
    }

    public String getPassenger_mob() {
        return passenger_mob;
    }

    public void setPassenger_mob(String passenger_mob) {
        this.passenger_mob = passenger_mob;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getStartPointAddress() {
        return StartPointAddress;
    }

    public void setStartPointAddress(String startPointAddress) {
        StartPointAddress = startPointAddress;
    }

    public String getEndPointAddress() {
        return EndPointAddress;
    }

    public void setEndPointAddress(String endPointAddress) {
        EndPointAddress = endPointAddress;
    }

    public String getFare() {
        return Fare;
    }

    public void setFare(String fare) {
        Fare = fare;
    }

    public String getStartPoint_Lat() {
        return StartPoint_Lat;
    }

    public void setStartPoint_Lat(String startPoint_Lat) {
        StartPoint_Lat = startPoint_Lat;
    }

    public String getStartPoint_Lon() {
        return StartPoint_Lon;
    }

    public void setStartPoint_Lon(String startPoint_Lon) {
        StartPoint_Lon = startPoint_Lon;
    }

    public String getEndPoint_Lat() {
        return EndPoint_Lat;
    }

    public void setEndPoint_Lat(String endPoint_Lat) {
        EndPoint_Lat = endPoint_Lat;
    }

    public String getEndPoint_Lon() {
        return EndPoint_Lon;
    }

    public void setEndPoint_Lon(String endPoint_Lon) {
        EndPoint_Lon = endPoint_Lon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
