package com.atsamour.habitatweave.models;

//import java.beans.*;
import java.io.Serializable;
import java.util.Date;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.Table;

/**
 *
 * @author AlariC
 */
//@Entity
//@Table
public class CaloriesHour implements Serializable {
    private static final long serialVersionUID = 622534243L;

    private int hour;
    private Date date;
    private Double calories;  //Total calories per hour
    private Double mi;  //Total moving intensity per hour

    public CaloriesHour() {
        calories=0.0;
        mi=0.0;
        hour=-1;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(Object hour) {
        this.hour = (int) hour;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = (Date) date;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }
    
    public void addCalories(Double calories) {
        this.calories += calories;
    }

    public Double getMi() {
        return mi;
    }

    public void setMi(Double mi) {
        this.mi = mi;
    }
    
    public void addMi(Double mi) {
        this.mi += mi;
    }
}
