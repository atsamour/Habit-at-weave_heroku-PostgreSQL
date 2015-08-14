/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */

package com.atsamour.habitatweave.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import com.atsamour.habitatweave.util.HibernateUtil;
import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.models.Room;
import com.atsamour.habitatweave.dao.ApplianceDAO;
import com.atsamour.habitatweave.dao.RoomDAO;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author AlariC
 */
//@WebServlet(name = "ArrangeRoomsServlet", urlPatterns = {"/arrangerooms"})
public class TestServlet extends HttpServlet {

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

        response.setContentType("text/html");// read password hash and salt from db

        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        try {
            ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            List<Appliance> appliances;
            appliances = applianceDAO.getAppliancesByUserId( 5 );

            RoomDAO roomDAO = new RoomDAO(hiberSession);
            final List<Room> rooms;
            rooms = roomDAO.getRoomsByUserId( 5 );
            
            int width = (100-rooms.size()*2) / (rooms.size()+1); // *2% margin size
            HttpSession session = request.getSession();
            session.setAttribute("width", width);
            session.setAttribute("rooms", rooms);
            session.setAttribute("appliances", appliances);
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

        HttpSession session = request.getSession();
        //appliances already saved @ session by doGet
        final List<Appliance> appliances = (List<Appliance>) session.getAttribute("appliances");
        Map m = request.getParameterMap();
        Set s = m.entrySet();
        Iterator it = s.iterator();

        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
        try {
            while (it.hasNext()) { //iterates thought rooms
                Map.Entry<String, String[]> entry = 
                        (Map.Entry<String, String[]>) it.next();

                String room_id_string = entry.getKey();
                int room_id = Integer.parseInt(room_id_string.replaceAll("[\\[\\]]", ""));
                String[] appliance_ids = entry.getValue();

                ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
                for (String appliance_id : appliance_ids) { //iterates thought appliances
                    for (Appliance test : appliances) {
                        if (!appliance_id.equals("") && test.getId() == Integer.parseInt(appliance_id)) {
                            test.setRoom_id(room_id);
                            applianceDAO.update(test);
                        }
                    }
                }
            }
        } finally {
            hiberSession.getTransaction().commit();
            if (hiberSession.isOpen()) {
                hiberSession.close();
            }
        }
        response.setStatus(200);
    }

}
