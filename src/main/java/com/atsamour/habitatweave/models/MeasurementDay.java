package com.atsamour.habitatweave.models;

import java.io.Serializable;
import java.util.Date;

public class MeasurementDay implements Serializable {
    private static final long serialVersionUID = 7232433L;
    
    private Date date;
    private Double SleepLatency;
    private Double TotalTimeDeepSleep;
    private Double TotalTimeInBedButAwake;
    private Double TotalTimeShallowSleep;

    public MeasurementDay() {
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = (Date) date;
    }

    public Double getSleepLatency() {
        return SleepLatency;
    }

    public void setSleepLatency(Double SleepLatency) {
        this.SleepLatency = SleepLatency;
    }

    public Double getTotalTimeDeepSleep() {
        return TotalTimeDeepSleep;
    }

    public void setTotalTimeDeepSleep(Double TotalTimeDeepSleep) {
        this.TotalTimeDeepSleep = TotalTimeDeepSleep;
    }

    public Double getTotalTimeInBedButAwake() {
        return TotalTimeInBedButAwake;
    }

    public void setTotalTimeInBedButAwake(Double TotalTimeInBedButAwake) {
        this.TotalTimeInBedButAwake = TotalTimeInBedButAwake;
    }

    public Double getTotalTimeShallowSleep() {
        return TotalTimeShallowSleep;
    }

    public void setTotalTimeShallowSleep(Double TotalTimeShallowSleep) {
        this.TotalTimeShallowSleep = TotalTimeShallowSleep;
    }

}
