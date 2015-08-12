/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.dao.ApplianceDAO;
import com.atsamour.habitatweave.dao.WorkToDoDAO;
import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.models.WorkToDo;
import com.atsamour.habitatweave.util.HibernateUtil;
import com.atsamour.habitatweave.util.AutoCompleteData;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

/**
 *
 * @author AlariC
 */
public class TestServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        // read password hash and salt from db

        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        try {
            WorkToDoDAO workToDoDAO = new WorkToDoDAO(hiberSession);
            final List<WorkToDo> workToDoList;
            workToDoList = workToDoDAO.getAllPendingWorks(Calendar.getInstance());
            
            final List<WorkToDo> workOldList;
            
            //vvvv show works to do in 1 min instead of ALL the old
            //workOldList = workToDoDAO.getWorksToDo(Calendar.getInstance());
            
            workOldList = workToDoDAO.getWorksOld(Calendar.getInstance());
            
            ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            final List<Appliance> appliances;
            appliances = applianceDAO.getAppliancesByUserId(5);
            
            
            request.setAttribute("appliances", appliances);
            request.setAttribute("workToDoList", workToDoList);
            request.setAttribute("workOldList", workOldList);
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/test.jsp");
            rd.forward(request, response);

        } finally {
            hiberSession.getTransaction().commit();
            if (hiberSession.isOpen()) {
                hiberSession.close();
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(request.getParameter("appliance")!=null && !request.getParameter("appliance").isEmpty() )
        {
            String description = request.getParameter("description");
            int appliance = Integer.parseInt( request.getParameter("appliance") );
            String action = request.getParameter("action");
            String dateString = request.getParameter("date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            
            Date date = new Date();
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException ex) {
                Logger.getLogger(SchedulesServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);  
            
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            try {
                createWork(session, description, appliance, action, cal);
                request.setAttribute("message", "Work created.");
            } finally {
                session.getTransaction().commit();
                if (session.isOpen()) {
                    session.close();
                }
            }
        } else {
            request.setAttribute("message", "Please fill form");
        }
        
        doGet(request,response);
    }
    
    private void createWork(Session session, String description,
                            int appliance, String action, Calendar date) {
        WorkToDo work = new WorkToDo();
        work.setDescription(description);
        work.setDone(0);
        work.setDateToDo(date);
        
        work.setCommand("/awesome?sensorID=" + appliance + "&action=" + action);

        session.save(work);

        System.err.println("Work with Description:" + work.getDescription() + " saved.");
    }
    
}
