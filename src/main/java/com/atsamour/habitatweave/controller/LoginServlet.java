package com.atsamour.habitatweave.controller;

import com.atsamour.habitatweave.auth.MySaltedAuthentificationInfo;
import com.atsamour.habitatweave.dao.UserDAO;
import com.atsamour.habitatweave.models.User;
import com.atsamour.habitatweave.util.HibernateUtil;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
//import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.hibernate.Session;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = -6856291352568121673L;

    public LoginServlet() {
        // init shiro - place this e.g. in the constructor
        Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request,
                HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (request.getParameter("logout") != null) {
            org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
            currentUser.logout();
        }

        try {
            request.getRequestDispatcher("/login.jsp").include(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");

        String email = request.getParameter("email");
        String pwd = request.getParameter("p");
        if (email == null || pwd == null) {
            request.setAttribute("message", "wrong parameters");
        } else {
            boolean b = tryLogin(email, pwd, false);
            if (b) {                
                request.getSession().setAttribute("currentEmail", getCurrentUserEmail());
                request.getSession().setAttribute("currendId", getCurrentUserId());
                //redirect after sucessfull auth
                request.getRequestDispatcher("/index").forward(request, response);
            } else {
                request.setAttribute("message", "wrong email/pwd or an error...");
            }
        }

        // draw JSP
        try {
            request.getRequestDispatcher("/login.jsp").include(request, response);
        } catch (ServletException e) {
                e.printStackTrace();
        }
    }

    // login
    public boolean tryLogin(String username, String password, Boolean rememberMe) {
        // get the currently executing user:
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();

        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            // this is all you have to do to support 'remember me' (no config -
            // built in!):
            token.setRememberMe(rememberMe);

            try {
                currentUser.login(token);
                System.out.println("User ["+ currentUser.getPrincipal().toString()
                                + "] logged in successfully.");

                // save current username in the session, so we have access to
                // our User model
                currentUser.getSession().setAttribute("username", username);
                
                //getting user id...
                Session hiberSession = HibernateUtil.getSessionFactory().openSession();
                hiberSession.beginTransaction();
                String id="";
                try {
                    UserDAO userDAO = new UserDAO(hiberSession);
                    final User user = userDAO.getUserByEmail(username);
                    id = Integer.toString( user.getId() );
                } finally {
                    hiberSession.getTransaction().commit();
                    if (hiberSession.isOpen()) hiberSession.close();
                }
                currentUser.getSession().setAttribute("id", id);
                return true;
            } catch (UnknownAccountException uae) {
                System.out.println("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                System.out.println("Password for account " + token.getPrincipal()
                             + " was incorrect!");
            } catch (LockedAccountException lae) {
                System.out.println("The account for username "+ token.getPrincipal()
                    + " is locked.  " + "Please contact your administrator to unlock it.");
            }
        } else {
            return true; // already logged in
        }

        return false;
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
