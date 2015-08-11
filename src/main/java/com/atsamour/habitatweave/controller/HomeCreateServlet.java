/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.hibernate.Session;

import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.models.Room;
import com.atsamour.habitatweave.util.HibernateUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//@WebServlet(urlPatterns = {"/homecreate"})
public class HomeCreateServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

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
        response.setContentType("text/html;charset=UTF-8");
        
	request.setAttribute("currentEmail", getCurrentUserEmail());
        
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/homecreate.jsp");
        rd.forward(request, response);
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
        
        //request.setAttribute("message", "please give description and vendor name");
        
        // new room
        if (//!request.getParameter("room").isEmpty() &&
                request.getParameter("name")!=null 
                && !request.getParameter("name").isEmpty()) {

            String name = request.getParameter("name");
            String user_id = getCurrentUserId();
            
            System.out.println(user_id);
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            try {
                createRoom(session, name, Integer.parseInt(user_id) );
                request.setAttribute("message", "Room created.");
                request.setAttribute("alert", "success");
            } finally {
                session.getTransaction().commit();
                if (session.isOpen()) {
                    session.close();
                }
            }
        // new appliance
        } else if(request.getParameter("description")!=null && !request.getParameter("description").isEmpty() )
        {
            String description = request.getParameter("description");
            String vendorname = request.getParameter("vendorname");     
            String optionsRadios = request.getParameter("optionsRadios");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = new Date();
            String adddate=dateFormat.format(date);
            String user_id = getCurrentUserId();
            int room_id = 0; //no room_id yet assigned
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            try {
                createAppliance(session, description, vendorname, optionsRadios,
                        adddate, room_id, Integer.parseInt(user_id) );
                request.setAttribute("message", "Appliance created.");
                request.setAttribute("alert", "success");
            } finally {
                session.getTransaction().commit();
                if (session.isOpen()) {
                    session.close();
                }
            }
        } else {
            request.setAttribute("message", "Please fill form");
            request.setAttribute("alert", "warning");
        }

        // draw JSP
        try {
            request.getRequestDispatcher("/secure/homecreate.jsp").include(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
    
    private void createAppliance(Session session, String description, String vendorname,
                        String optionsRadios, String adddate, int room_id, int user_id) {
        Appliance appliance = new Appliance();
        appliance.setDescription(description);
        appliance.setVendorname(vendorname);
        appliance.setType(optionsRadios);
        appliance.setAdddate(adddate);
        appliance.setRoom_id(room_id);
        appliance.setUser_id(user_id);

        session.save(appliance);

        System.err.println("Appliance with Description:" + appliance.getDescription() + " saved.");
    }
    
    private void createRoom(Session session, String name, int user_id) {
        Room room = new Room();
        room.setName(name);
        room.setUser_id(user_id);
        
        session.save(room);

        System.err.println("Room with name:" + room.getName() + " saved.");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    public String getCurrentUserEmail() {
	org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

	if (currentUser.isAuthenticated()) {
            String mail = (String) currentUser.getSession().getAttribute("username");
		return mail;
	} else {
		return null;
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

