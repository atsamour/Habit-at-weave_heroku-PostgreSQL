/**
 * Copyright (C) 2014. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * @author      Arkadios Tsamourliadis
 */
package com.atsamour.habitatweave.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import com.atsamour.habitatweave.models.MeasurementDay;
import com.atsamour.habitatweave.util.HibernateUtil;
/**
 *
 * @author AlariC
 */
public class SleepServlet extends HttpServlet {
    
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
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("SELECT `start_time` FROM measurement WHERE type = 'SleepLatency' ORDER BY start_time ASC LIMIT 1");
                
        List<Object> result = query.list();
        Date date = new Date();
        date = (java.sql.Timestamp)( result.get(0) );
        request.getSession().setAttribute("minD", date);
        query = session.createSQLQuery("SELECT `start_time` FROM measurement WHERE type = 'SleepLatency' ORDER BY start_time DESC LIMIT 1");
        result = query.list();
        date = (java.sql.Timestamp)( result.get(0) );  
        request.getSession().setAttribute("maxD", date); 
        
        request.setAttribute("hide", "hide");
        if (session.isOpen()) {
                session.close();
            }
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/sleep.jsp");
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
        
        if(request.getParameter("date1")!=null && !request.getParameter("date1").isEmpty() &&
                request.getParameter("date2")!=null && !request.getParameter("date2").isEmpty() )
        {
            String date1str = request.getParameter("date1");
            String date2str = request.getParameter("date2");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = dateFormat.parse(date1str);
                date2 = dateFormat.parse(date2str);
            } catch (ParseException ex) {
                Logger.getLogger(SchedulesServlet.class.getName()).log(Level.SEVERE, null, ex);
            }            
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String person_id="10000";//????????????
            List<Object[]> result = getSleepMeasurement(session, person_id, date1, date2);
            List<MeasurementDay> mdList = new ArrayList();
            //Date currDate = new Date(0);
            MeasurementDay md= new MeasurementDay();
            //Iterate throught rows and create a MeasurementDay Object every new date
            for(Object[] row : result){
                //new day
                if (md.getDate() == null) {
                    md.setDate(row[0]);
                    setTypeValue(md, row[1].toString(), row[2].toString());
                    //same day, new measurement
                } else if ( md.getDate().compareTo((Date)row[0]) == 0 ){ 
                    setTypeValue(md, row[1].toString(), row[2].toString());
                } else if (md.getDate().compareTo((Date)row[0]) != 0 ) {
                    mdList.add(md);
                    md= new MeasurementDay();
                    md.setDate(row[0]);
                    setTypeValue(md, row[1].toString(), row[2].toString());
                }
            }
            mdList.add(md); //adding the last Day
            
            //Rearange data from /day to /SleepType
            List<List<Double>> measurementsPerType = new ArrayList();
            for(int i=0;i<4;i++){
                List<Double> typeMeasurement = new ArrayList();
                for(MeasurementDay day : mdList){
                    if(i==0)
                        typeMeasurement.add(day.getSleepLatency());
                    if(i==1)
                        typeMeasurement.add(day.getTotalTimeDeepSleep());
                    if(i==2)
                        typeMeasurement.add(day.getTotalTimeInBedButAwake());
                    if(i==3)
                        typeMeasurement.add(day.getTotalTimeShallowSleep());
                }
                measurementsPerType.add(typeMeasurement);
            }
            List<String> dates = new ArrayList();
            for(MeasurementDay day : mdList){
                dates.add(day.getDate().toString());
            }
            request.setAttribute("measurementsPerType", measurementsPerType);
            request.setAttribute("dates", dates);
            
            if (session.isOpen()) {
                session.close();
            }

        } else {
            request.setAttribute("message", "No date selected");
        }
        
        request.setAttribute("hide", "");
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/sleep.jsp");
        rd.forward(request, response);
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

    private List<Object[]> getSleepMeasurement(Session session, String person_id, Date date1, Date date2) {
        Query query = session.createSQLQuery(
            "select `date`, `type`, `value` from measurement "
                    + "WHERE measurement.person_id=:pid AND type IN ( 'SleepLatency', 'TotalTimeDeepSleep', 'TotalTimeInBedButAwake', 'TotalTimeShallowSleep') "
                    + "AND date BETWEEN :date1 AND :date2" )//.addScalar("type", StringType.INSTANCE)
        //.addEntity(Date.class)
        .setString("pid", person_id)
        .setDate("date1", date1)
        .setDate("date2", date2);
        List<Object[]> result = query.list();
        return result;
    }
    
    public void setTypeValue(MeasurementDay md, String type, String value ) {
        switch ( type ) {
            case "SleepLatency":
                md.setSleepLatency( Double.parseDouble(value) );
                break;
            case "TotalTimeDeepSleep":
                md.setTotalTimeDeepSleep( Double.valueOf(value) );
                break;
            case "TotalTimeInBedButAwake":
                md.setTotalTimeInBedButAwake(Double.valueOf(value) );
                break;
            case "TotalTimeShallowSleep":
                md.setTotalTimeShallowSleep(Double.valueOf(value) );
                break;
            default:
                //values_not_caught_above
        }
    }
}
