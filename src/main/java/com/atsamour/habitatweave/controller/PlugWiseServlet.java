/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.models.SensorInfo;
import com.atsamour.habitatweave.util.HibernateUtil;
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
import org.apache.shiro.SecurityUtils;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

/**
 *
 * @author AlariC
 */
//@WebServlet(name = "plugwise", urlPatterns = {"/plugwise"})
public class PlugWiseServlet extends HttpServlet {

    public void showCharts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        //Connection con = (Connection) getServletContext().getAttribute("DBConnection");
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;
        
        Session  session = HibernateUtil.getSessionFactory().getCurrentSession();
        SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
        ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();
                   
        //List of SensorInfo Object, each of one contains corresponding sensor probes
        //List<SensorInfo> _sensors = new ArrayList<SensorInfo>();
        List<String> _sensorIDs = new ArrayList<String>();
        
         //else {
            //sensor = _sensorIDs.get(0); }
        try {
            Connection con = connectionProvider.getConnection();
            ps = con.prepareStatement("SELECT DISTINCT `sensorID` FROM pwdata");
            rs = ps.executeQuery();
            while(rs.next()){
                //Retrieve by column name
                String sensorID  = rs.getString("sensorID");
                _sensorIDs.add(sensorID);
            }
            
            request.setAttribute("sensorIDs", _sensorIDs);
            
            
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
        
        try {
            String sensor = "";
            if(request.getParameter("sensor")!=null && !request.getParameter("sensor").isEmpty())
                sensor =  ( request.getParameter("sensor") );
            else sensor=_sensorIDs.get(0);
            
            Connection con2 = connectionProvider.getConnection();       
            
            ps2 = con2.prepareStatement("SELECT `sensorID`, SUM(pwdata.value) AS value,"
                    + " `date`, `time` FROM pwdata WHERE pwdata.sensorID = ? GROUP BY date, HOUR(time)");
            ps2.setString(1,sensor);
            rs2 = ps2.executeQuery();

            // Extract data from result set
            SensorInfo tmpSensor = new SensorInfo();
            while(rs2.next()){
                //Retrieve by column name
                String date = rs2.getString("date");
                String time = rs2.getString("time");
                float watts = rs2.getFloat("value");
                tmpSensor.addInfo(sensor, date, time, watts);
            }

            request.setAttribute("sensorObj", tmpSensor);
            request.setAttribute("sensor", sensor);

            //set last probe of selected sensor
            List<Float> watts = tmpSensor.getWatts();
            float probe = watts.get(watts.size() - 1);
            request.setAttribute("indication", probe);
            
            RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/plugwise.jsp");
            rd.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            //logger.error("Database connection problem");
            throw new ServletException("DB Connection problem.");
        }
        finally{
            try {
                if (rs2!=null){
                    rs2.close();
                }
                if (ps2!=null){
                    ps2.close();
                }
            } catch (SQLException e) {
                System.out.println("SQLException in closing PreparedStatement or ResultSet");
            }

        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
