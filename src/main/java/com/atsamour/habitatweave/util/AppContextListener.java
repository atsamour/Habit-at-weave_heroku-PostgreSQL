/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.atsamour.habitatweave.util;

import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.atsamour.habitatweave.util.ScheduledWorks;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
/**
 * Web application lifecycle listener.
 *
 * @author AlariC
 */
@WebListener()
public class AppContextListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                
//        ServletContext ctx = sce.getServletContext();
//    	
//    	//initialize DB Connection
//    	String dbURL = ctx.getInitParameter("dbURL");
//    	String user = ctx.getInitParameter("dbUser");
//    	String pwd = ctx.getInitParameter("dbPassword");
//    	
//    	try {
//            DBConnectionManager connectionManager = new DBConnectionManager(dbURL, user, pwd);
//            ctx.setAttribute("DBConnection", connectionManager.getConnection());
//            System.out.println("DB Connection initialized successfully.");
//	} catch (ClassNotFoundException e) {
//            e.printStackTrace();
//	} catch (SQLException e) {
//            e.printStackTrace();
//	}
        scheduler = Executors.newSingleThreadScheduledExecutor();
        //(action, delayFromStart, periodUnitsCount, TimeUnits)
        scheduler.scheduleAtFixedRate(new ScheduledWorks(), 0, 1, TimeUnit.MINUTES);
        //scheduler.scheduleAtFixedRate(new ScheduledWorks(), 10, 20, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        
//        Connection con = (Connection) sce.getServletContext().getAttribute("DBConnection");
//        try {
//            con.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
