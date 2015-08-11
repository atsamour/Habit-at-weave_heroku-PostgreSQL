/**
 * Copyright (C) 2015. All rights reserved.
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3;
 * Arkadios Tsamourliadis   https://github.com/atsamour/
 */
package com.atsamour.habitatweave.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.hibernate.Session;

import com.atsamour.habitatweave.models.User;
import com.atsamour.habitatweave.models.UserRole;
import com.atsamour.habitatweave.util.HibernateUtil;

/**
 * Servlet implementation class RegisterServlet
 */
//@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 5733722323174731486L;

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        // draw JSP
        try {
            request.getRequestDispatcher("/register.jsp").include(request,
                    response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String email = request.getParameter("email");
        String pwd = request.getParameter("p");
        String admin = request.getParameter("role_admin");
        if (email == null || pwd == null) {
            request.setAttribute("message", "wrong parameters");
        } else {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            try {
                registrate(session, email, pwd, admin != null);
                request.setAttribute("message", "User created.");
            } finally {
                session.getTransaction().commit();
                if (session.isOpen()) {
                    session.close();
                }
            }
        }

        // draw JSP
        try {
            request.getRequestDispatcher("/login.jsp").include(request,
                    response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private void generatePassword(User user, String plainTextPassword) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        Object salt = rng.nextBytes();

	// Now hash the plain-text password with the random salt and multiple
        // iterations and then Base64-encode the value (requires less space than Hex):
        String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt,
                1024).toBase64();

        user.setPassword(hashedPasswordBase64);
        user.setSalt(salt.toString());
    }

    private void registrate(Session session, String email, String plainTextPassword, boolean isAdmin) {
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);

        generatePassword(user, plainTextPassword);
        session.save(user);

        System.err.println("User with email:" + user.getEmail()
                + " hashedPassword:" + user.getPassword() + " salt:"
                + user.getSalt());

        // create role
        if (isAdmin) {
            UserRole role = new UserRole();
            role.setEmail(email);
            role.setRoleName("admin");
            session.save(role);
        }
    }

}
