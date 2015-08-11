/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.dao.ApplianceDAO;
import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.util.AutoCompleteData;
import com.atsamour.habitatweave.util.HibernateUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.hibernate.Session;

// Returns a json with appliances strartomg with "term"
public class AutoCompleteResponce extends HttpServlet {


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
            
        Session hiberSession = HibernateUtil.getSessionFactory().openSession();
        hiberSession.beginTransaction();
               
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            
            ApplianceDAO applianceDAO = new ApplianceDAO(hiberSession);
            final List<Appliance> appliances;
            appliances = applianceDAO.getAppliancesByUserId(Integer.parseInt(getCurrentUserId()));
            
            final List<AutoCompleteData> result = new ArrayList();
            
            String term = request.getParameter("term");
            term = term.toLowerCase();
            for (final Appliance appliance : appliances) {
                if (appliance.getDescription().toLowerCase().startsWith(term.toLowerCase())) {
                    result.add( new AutoCompleteData( appliance.getDescription(),
                            Integer.toString( appliance.getId()), appliance.getVendorname() ) );
                }
            }
            String jresp = new Gson().toJson(result);
            out.println(jresp);
            
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
