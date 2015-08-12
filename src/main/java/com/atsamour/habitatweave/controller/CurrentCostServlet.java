/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;

import com.atsamour.habitatweave.models.SensorInfo;
import com.atsamour.habitatweave.models.Appliance;
import com.atsamour.habitatweave.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

/**
 * Servlet implementation class CurrentCostServlet
 */
//@WebServlet(urlPatterns = {"/server"})
public class CurrentCostServlet extends HttpServlet {
    private static final long serialVersionUID = 15328L;
    
    public void showCharts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        //Connection con = (Connection) getServletContext().getAttribute("DBConnection");
        Session  session = HibernateUtil.getSessionFactory().getCurrentSession();
        SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
        ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();
          
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        //List of SensorInfo Object, each of one contains corresponding sensor probes
        List<SensorInfo> _sensors = new ArrayList();
        List<Integer> _sensorIDs = new ArrayList();

        try {
            Connection con = connectionProvider.getConnection();
//            ps = con.prepareStatement("SELECT `sensorID`, SUM(ccdata.phasesum) AS phasesum,"
//                    + " `date`, `time` FROM ccdata ORDER BY `sensorID`, date, HOUR(time)");
//              did not yet implemented select by sensorID
                        
            ps = con.prepareStatement("SELECT * FROM ccdata ORDER BY sensorID");
            rs = ps.executeQuery();

            // Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                int sensorID  = rs.getInt("sensorID");
                String date = rs.getString("date");
                String time = rs.getString("time");
                float watts = rs.getFloat("phasesum");

                if (_sensorIDs.contains(sensorID)){
                    _sensors.get( _sensorIDs.indexOf(sensorID) ).addInfo(date, time, watts);
                } else{                    
                    SensorInfo tmpSensor = new SensorInfo();
                    tmpSensor.addInfo(sensorID, date, time, watts);
                    _sensors.add(tmpSensor);
                    _sensorIDs.add(sensorID);
                }
            }

            //HttpSession session = request.getSession();
            request.setAttribute("sensorIDs", _sensorIDs);
            request.setAttribute("sensors", _sensors);

            int sensor;
            if(request.getParameter("sensor")!=null){
                sensor = Integer.parseInt ( request.getParameter("sensor") );
            } else {
                sensor = _sensorIDs.get(0);
            }

            request.setAttribute("sensor", sensor);
            request.setAttribute( "sensorObj", _sensors.get( _sensorIDs.indexOf(sensor) ) );

            //set last probe of selected sensor
            List<Float> watts = _sensors.get( _sensorIDs.indexOf(sensor) ).getWatts();
            float probe = watts.get(watts.size() - 1);
            //System.out.println(probe);
            //request.setAttribute("indication", Float.toString(probe));
            request.setAttribute("indication", probe);
            

            
            //System.out.println(request.getRequestURI());
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/currentcost.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            //logger.error("Database connection problem");
            throw new ServletException("DB Connection problem.");
        }
        finally{
            try {
                if (rs!=null){
                    rs.close();
                }
                if (ps!=null){
                    ps.close();
                }
            } catch (SQLException e) {
                System.out.println("SQLException in closing PreparedStatement or ResultSet");
            }

        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
//	request.setAttribute("currentEmail", getCurrentUserEmail());
//	org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
//	request.setAttribute("adminAccess", currentUser.isPermitted("admin:access"));
        
        response.setContentType("text/html;charset=UTF-8");       
        showCharts(request, response); 
  }
    
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    public String getCurrentUserEmail() {
	org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

	if (currentUser.isAuthenticated()) {
            String mail = (String) currentUser.getSession().getAttribute("username");
		return mail;
	} else {
		return null;
	}
    }

}
