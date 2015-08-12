/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * @author      Arkadios Tsamourliadis
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.models.CaloriesHour;
import com.atsamour.habitatweave.util.HibernateUtil;
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

public class ActivityServlet extends HttpServlet {

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
        request.setAttribute("hide", "hide");
        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        //Retrives the min and max date with available data form the measurement table
        Query query = session.createSQLQuery("SELECT start_time FROM measurement WHERE type = 'Calories' ORDER BY start_time ASC LIMIT 1");
        List<Object> result = query.list();
        Date date = new Date();
        date = (java.sql.Timestamp)( result.get(0) );
        //Attributes used at datetimepicker JavaScript
        request.getSession().setAttribute("minD", date);
        query = session.createSQLQuery("SELECT start_time FROM measurement WHERE type = 'Calories' ORDER BY start_time DESC LIMIT 1");
        result = query.list();
        date = (java.sql.Timestamp)( result.get(0) );  
        request.getSession().setAttribute("maxD", date);               
        
        if (session.isOpen()) {
            session.close();
        }    
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/activity.jsp");
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
            Session session=null;
            List<Object[]> result=new ArrayList();
            try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            session.beginTransaction();
            String person_id="10000";//????????????
            // after exec it will look like this: result[] = [`date`, `value`, `start_time`]
            result = getActivityMeasurement(session, person_id, date1, date2);
            }
            catch (  Exception e) {
               e.printStackTrace();
            }
            finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
            List<CaloriesHour> chList = new ArrayList(); //for calories
            //Date currDate = new Date(0);
            CaloriesHour ch= new CaloriesHour();
            //Iterate throught rows and create a MeasurementDay Object every new hour
            for(Object[] row : result){
                //new day
                if (ch.getHour() == -1 ) {
                    //ch.setHour(((Timestamp)row[2]).getHours() ); for the oldquery                    
                    ch.setHour( row[2] );
                    ch.setDate(row[0]);
                    if (row[3].equals("Calories"))
                        ch.addCalories( (Double) row[1] ); //or setCalories
                    else
                        ch.addMi( (Double) row[1] ); //or setCalories
                           
                    //same day, new measurement
                } else if ( ch.getHour() == (int) row[2] ){
                    
                    if (row[3].equals("Calories"))
                        ch.addCalories( Double.valueOf( row[1].toString() ) );
                    else
                        ch.addMi( Double.valueOf( row[1].toString() ) );
                } else if ( ch.getHour() != (int)row[2] ) {
                    chList.add(ch);
                    ch= new CaloriesHour();
                    //ch.setHour(((Timestamp)row[2]).getHours() );  old query
                    ch.setHour( row[2] );
                    ch.setDate(row[0]);
                    if (row[3].equals("Calories"))
                        ch.addCalories( Double.valueOf( row[1].toString() ) ); //or setCalories
                    else
                        ch.addMi( Double.valueOf( row[1].toString() ) );
                }
            }
            chList.add(ch); //adding the last Day
            
            int year = chList.get(0).getDate().getYear()+1900;
            int month = chList.get(0).getDate().getMonth();
            int day = chList.get(0).getDate().getDate();
            String minDate = Integer.toString(year)+", "+Integer.toString(month)
                    +", "+Integer.toString(day);
            request.setAttribute("minDate", minDate);
            
            year = chList.get(chList.size()-1).getDate().getYear()+1900;
            month = chList.get(chList.size()-1).getDate().getMonth();
            day = chList.get(chList.size()-1).getDate().getDate();
            String maxDate = Integer.toString(year)+", "+Integer.toString(month)
                    +", "+Integer.toString(day);
            request.setAttribute("maxDate", maxDate);
            
            request.setAttribute("chList", chList);
            
            
            //Rearange data from /day to /SleepType
//            List<List<Double>> measurementsPerType = new ArrayList();
//            for(int i=0;i<4;i++){
//                List<Double> typeMeasurement = new ArrayList();
//                for(CaloriesHour hour : chList){
//                    if(i==0)
//                        typeMeasurement.add(day.getSleepLatency());
//                    if(i==1)
//                        typeMeasurement.add(day.getTotalTimeDeepSleep());
//                    if(i==2)
//                        typeMeasurement.add(day.getTotalTimeInBedButAwake());
//                    if(i==3)
//                        typeMeasurement.add(day.getTotalTimeShallowSleep());
//                }
//                measurementsPerType.add(typeMeasurement);
//            }
//            List<String> dates = new ArrayList();
//            for(MeasurementDay day : mdList){
//                dates.add(day.getDate().toString());
//            }
            //request.setAttribute("dates", dates);
            

        } else {
            request.setAttribute("message", "No date selected");
        }
        
        request.setAttribute("hide", "");
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/activity.jsp");
        rd.forward(request, response);
    }
    
    public List<Object[]> getActivityMeasurement(Session session, 
            String person_id, Date date1, Date date2) {
        Query query = session.createSQLQuery(
             "SELECT date, SUM(measurement.value) AS value, HOUR(measurement.start_time)"
                     + " as start_time, type FROM measurement WHERE "
                     + "measurement.person_id=:pid AND type IN ( 'Calories', 'MovingIntensity' )"
                     + " AND date BETWEEN :date1 AND :date2 GROUP BY date, HOUR(start_time), type")
        .setString("pid", person_id)
        .setDate("date1", date1)
        .setDate("date2", date2);
        List<Object[]> result = query.list();
        return result;
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

}
