/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * @author      Arkadios Tsamourliadis
 */

package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.dao.ApplianceDAO;
import com.atsamour.habitatweave.dao.RoomDAO;
import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.models.Room;
import com.atsamour.habitatweave.util.HibernateUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author AlariC
 */
//@WebServlet(name = "switchplugs", urlPatterns = {"/switchplugs"})
public class SwitchPlugsServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");// read password hash and salt from db

        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        String baseURL = "";
             
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            final List<Appliance> appliances;
            appliances = applianceDAO.getAppliancesByUserId( Integer.parseInt(getCurrentUserId()) );

            RoomDAO roomDAO = new RoomDAO(hiberSession);
            final List<Room> rooms = roomDAO.getRoomsByUserId( Integer.parseInt(getCurrentUserId()) );
           
            Query query = hiberSession.createSQLQuery("select `path` from options "
                    + "WHERE options.user_id= :pid ORDER BY time  DESC LIMIT 1" )
                    .setInteger("pid", Integer.parseInt(getCurrentUserId()));
            List<Object> result = query.list();
            
            baseURL = (String) result.get(0);
            
            HttpSession session = request.getSession();
            
            session.setAttribute("baseURL", baseURL);
            session.setAttribute("rooms", rooms);
            session.setAttribute("appliances", appliances);
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/switchplugs.jsp");
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
