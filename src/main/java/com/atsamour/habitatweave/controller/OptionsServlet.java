/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.util.HibernateUtil;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Query;
import org.hibernate.Session;

public class OptionsServlet extends HttpServlet {

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

        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/options.jsp");
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
        if (request.getParameter("path") != null
                && !request.getParameter("path").isEmpty()) {

            String path = request.getParameter("path");
            String user_id = getCurrentUserId();

            System.out.println(user_id);
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            try {
                java.util.Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());

                Query query = session.createSQLQuery("INSERT INTO options (user_id, path, time) VALUES (:user_id, :path, :time)")
                        .setInteger("user_id", Integer.parseInt(user_id))
                        .setString("path", path)
                        .setTimestamp("time", timestamp);
                query.executeUpdate();
            } finally {
                session.getTransaction().commit();
                request.setAttribute("message", "Path inserted successfully");
                request.setAttribute("alert", "success");
                if (session.isOpen()) {
                    session.close();
                }
            }
            // new appliance
        } else {
            request.setAttribute("message", "Please fill form");
            request.setAttribute("alert", "warning");
        }

        // draw JSP
        try {
            request.getRequestDispatcher("/secure/options.jsp").include(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
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
