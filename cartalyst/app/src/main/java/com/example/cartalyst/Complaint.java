package com.example.cartalyst;

import java.io.Serializable;

public class Complaint implements Serializable {
    public String id,locality,cid,aid,uid,address,img,resolver_id,resolver;
    public int numOfCows;
    public double latitude,longitude,distanceToComplaint;

    public Complaint() {

    }
    public double getDistance() {
        return distanceToComplaint;
    }
}
