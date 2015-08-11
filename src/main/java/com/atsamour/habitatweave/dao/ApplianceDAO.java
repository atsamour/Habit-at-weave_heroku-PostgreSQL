package com.atsamour.habitatweave.dao;

import java.util.List;

import org.hibernate.Session;

import com.atsamour.habitatweave.models.Appliance;

public class ApplianceDAO {

    private final Session session;

    public ApplianceDAO(Session s) {
        session = s;
    }

    public List<Appliance> getAllAppliances() {
        @SuppressWarnings("unchecked")
        List<Appliance> appliances = session.createQuery("from Appliance").list();
        return appliances;
    }
    
    public List<Appliance> getAppliancesByUserId(int id) {
        @SuppressWarnings("unchecked")
        List<Appliance> appliances = session
                .createQuery("from Appliance where user_id=?").setInteger(0, id).list();
        return appliances;
    }
    
    public Appliance getApplianceById(int id) {
        //@SuppressWarnings("unchecked")
        //Appliance appliance = (Appliance) session.createQuery("from Appliance where id=?").setInteger(0, id).uniqueResult();
        Appliance a = (Appliance)session.load(Appliance.class, id);
        return a;
    }
    
    public Appliance getApplianceById(String id) {
        //@SuppressWarnings("unchecked")
        //Appliance appliance = (Appliance) session.createQuery("from Appliance where id=?").setInteger(0, id).uniqueResult();
        Appliance a = (Appliance)session.load(Appliance.class, id);//??????? implement "find by vendorname (string:id)
        return a;
    }
    
    public void update(Appliance a) {
        session.update(a);
    }
}
