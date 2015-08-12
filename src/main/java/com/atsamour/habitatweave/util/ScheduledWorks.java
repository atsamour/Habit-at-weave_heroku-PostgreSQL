/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * @author      Arkadios Tsamourliadis
 */

package com.atsamour.habitatweave.util;

import com.atsamour.habitatweave.dao.WorkToDoDAO;
import com.atsamour.habitatweave.models.WorkToDo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Query;

import org.hibernate.Session;

public class ScheduledWorks implements Runnable {

    @Override
    public void run() {
        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        
        System.out.println("ScheduledWorks executed...");
        try {            
            WorkToDoDAO workToDoDAO = new WorkToDoDAO(hiberSession);
            final List<WorkToDo> workToDoList;
            //workToDoList = workToDoDAO.getWorksToDo(Calendar.getInstance());
            workToDoList = workToDoDAO.getWorksToDoByUserId(Integer.parseInt(getCurrentUserId()), Calendar.getInstance());
            
            if (workToDoList != null){
                for (WorkToDo currWork : workToDoList) {
                    executeWork(currWork);       
                }
            }
                    
        } finally {
            hiberSession.getTransaction().commit();
            if (hiberSession.isOpen()) {
                hiberSession.close();
            }
        }
    }
    
    //hypothetical scheduled work execution by calling a w.action servlet
    private void executeWork(WorkToDo w){
        String baseURL = "";
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();  
        try {
            //retrives the user's last inserted aWESoME path
            Query query = session.createSQLQuery("select path from options "
                    + "WHERE options.user_id= :pid ORDER BY time  DESC LIMIT 1" )
                    .setInteger("pid", Integer.parseInt(getCurrentUserId()));
            List<Object> result = query.list();            
            baseURL = (String) result.get(0);
        
        } finally {
            session.getTransaction().commit();
            if (session.isOpen()) {
                session.close();
            }
        }
        
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {            
            url = new URL( baseURL + w.getCommand() );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
            System.out.println(result);
        } catch (IOException ex) {
            Logger.getLogger(ScheduledWorks.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String getCurrentUserId() {
	org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

	if (currentUser.isAuthenticated()) {
            String id = (String) currentUser.getSession().getAttribute("id");
		return id;
	} else {
		return null;
	}
    }
    
   
}