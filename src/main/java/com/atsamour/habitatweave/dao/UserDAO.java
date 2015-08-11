package com.atsamour.habitatweave.dao;

import org.hibernate.Session;

import com.atsamour.habitatweave.models.User;

public class UserDAO {

    private final Session session;

    public UserDAO(Session s) {
        session = s;
    }

    public User getUserByEmail(String email) {
        User user = (User) session.createQuery("from User where email=?")
                .setString(0, email).uniqueResult();
        return user;
    }
}
