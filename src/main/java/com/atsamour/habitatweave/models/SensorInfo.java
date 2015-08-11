/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.models;

import com.atsamour.habitatweave.dao.ApplianceDAO;
import com.atsamour.habitatweave.util.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

public class SensorInfo implements Serializable {
    
    private int sensorID;
    private String sensorIDString;
    private Appliance appliance;
    private String description;
    private String vendorname;
    private List<String> date;
    private List<String> time;
    private List<Float> watts;
    //private long elapsedTime; =Date.getTime();
    
    public SensorInfo() {
        this.date = new ArrayList<String>(100);
        this.time = new ArrayList<String>(100);
        this.watts = new ArrayList<Float>(100);
    }
    
    public void addInfo(String date, String time, float watts){
        this.date.add(date);
        this.time.add(time);
        this.watts.add(watts);
    }
    
    public void addInfo(int sensorID, String date, String time, float watts){
        this.sensorID = sensorID;
        this.date.add(date);
        this.time.add(time);
        this.watts.add(watts);
        
        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            this.appliance = applianceDAO.getApplianceById(sensorID);
            this.description = appliance.getDescription();
            this.vendorname = appliance.getVendorname();
        } finally {
            hiberSession.getTransaction().commit();
            if (hiberSession.isOpen()) {
                hiberSession.close();
            }
        }
    }
    
    public void addInfo(String sensorID, String date, String time, float watts){
        this.sensorIDString = sensorID;
        this.date.add(date);
        this.time.add(time);
        this.watts.add(watts);
        
        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        try {
            //ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            //this.appliance = applianceDAO.getApplianceById(sensorID); //??????? uncomment after implementing find by vendordescr
            this.description = "not specified";
            this.vendorname = "Sample vendorname";
        } finally {
            hiberSession.getTransaction().commit();
            if (hiberSession.isOpen()) {
                hiberSession.close();
            }
        }
    }
    
    public void setAppliance(Appliance a){
        this.appliance=a;
    }
    
    public Appliance getAppliance(){
        return appliance;
    }
    
    public String getDescription(){
        return description;
    }
    
    public String getVendorname(){
        return vendorname;
    }
    
    public int getSensorID(){
        return sensorID;
    }

    public String getSensorIDString() {
        return sensorIDString;
    }

    public void setSensorIDString(String sensorIDString) {
        this.sensorIDString = sensorIDString;
    }
    
    public List<String> getDate(){
        return date;
    }
    public List<String> getTime(){
        return time;
    }
    public List<Float> getWatts(){
        return watts;
    }
}
