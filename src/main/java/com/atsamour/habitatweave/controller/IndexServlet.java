/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.models.CaloriesHour;
import com.atsamour.habitatweave.models.MeasurementDay;
import com.atsamour.habitatweave.models.SensorInfo;
import com.atsamour.habitatweave.util.HibernateUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

/**
 *
 * @author AlariC
 */
//@WebServlet(name = "index", urlPatterns = {"/index"})
public class IndexServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        
        Session  session = HibernateUtil.getSessionFactory().getCurrentSession();
        
        SessionFactoryImplementor sessionFactoryImplementation = (SessionFactoryImplementor) session.getSessionFactory();
        ConnectionProvider connectionProvider = sessionFactoryImplementation.getConnectionProvider();        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        //List of SensorInfo Object, each of one contains corresponding sensor probes
        List<SensorInfo> _sensors = new ArrayList<SensorInfo>();
        List<Integer> _sensorIDs = new ArrayList<Integer>();

        
        try {
            Connection con = connectionProvider.getConnection();
            ps = con.prepareStatement("SELECT * FROM ccdata WHERE sensorID = '0'");
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
            request.setAttribute("indication", probe);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            
            Date date1 = new Date();
            Date date2 = new Date();
            try {
                date1 = dateFormat.parse("2014/05/21");
                date2 = dateFormat.parse("2014/05/28");
            } catch (ParseException ex) {
                Logger.getLogger(SchedulesServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
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
            
//            Session session2 = HibernateUtil.getSessionFactory().openSession();
//            session2.beginTransaction();
            
            //session = HibernateUtil.getSessionFactory().getCurrentSession();
            //session.flush();
            //session.beginTransaction();
            try {
                date1 = dateFormat.parse("2014/07/09");
                date2 = dateFormat.parse("2014/07/28");
            } catch (ParseException ex) {
                Logger.getLogger(SchedulesServlet.class.getName()).log(Level.SEVERE, null, ex);
            }    
            List<Object[]> result2 = getActivityMeasurement(session, person_id, date1, date2);
            List<CaloriesHour> chList = new ArrayList();
            CaloriesHour ch= new CaloriesHour();
            //Iterate throught rows and create a MeasurementDay Object every new hour
            for(Object[] row : result2){
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
            
            request.setAttribute("chList", chList);
            
            if (session.isOpen()) {
                session.close();
            }
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
        
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/secure/index.jsp");
        rd.forward(request, response);
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
        processRequest(request, response);
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
        processRequest(request, response);
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
    
    
    private List<Object[]> getActivityMeasurement(Session session, String person_id, Date date1, Date date2) {
        Query query = session.createSQLQuery(
             "SELECT `date`, SUM(measurement.value) AS value, HOUR(measurement.start_time) as start_time, "
                     + "`type` FROM measurement WHERE measurement.person_id=:pid "
                     + "AND type IN ( 'MovingIntensity' ) AND date BETWEEN :date1 AND :date2 "
                     + "GROUP BY date, HOUR(start_time)")
//            "select `date`, `type`, SUM(measurement.value) AS value from measurement "
//                    + "WHERE measurement.person_id=:pid AND type IN ( 'SleepScore' ) "
//                    + "AND date BETWEEN :date1 AND :date2 GROUP BY date, HOUR(start_time)" )//.addScalar("type", StringType.INSTANCE)
        .setString("pid", person_id)
        .setDate("date1", date1)
        .setDate("date2", date2);
        List<Object[]> result = query.list();
        return result;
    }
}
