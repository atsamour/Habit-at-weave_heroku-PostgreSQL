package com.atsamour.habitatweave.dao;

import org.hibernate.Session;

import com.atsamour.habitatweave.models.User_app;

public class User_appDAO {

    private final Session session;

    public User_appDAO(Session s) {
        session = s;
    }

    public User_app getUserByEmail(String email) {
        User_app user = (User_app) session.createQuery("from User_app where email=?")
                .setString(0, email).uniqueResult();
        //User_app user = (User_app)  session.createQuery("from user_app where email = :email").setParameter("email", email).uniqueResult();
        return user;
    }
}
