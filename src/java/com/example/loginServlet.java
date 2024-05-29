/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author PC GAMING
 */
@WebServlet(urlPatterns = {"/loginServlet"})
public class loginServlet extends HttpServlet {

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
      String username = request.getParameter("username");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Replace with your Oracle database connection parameters
          String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
            String dbUrl = "jdbc:oracle:thin:@localhost:1521:xe";
          
            String dbUser = "HOSPITAL";
            String dbPassword = "1234";


            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            // Prepare and execute SQL statement to validate user
            String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ? AND PASSWORD = ? AND USER_TYPE = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, userType);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
                if ("Admin".equals(userType)) {
                    out.write("{\"status\":\"success\",\"redirect\":\"HomeAdmin.html\"}");
                } else if ("Enfermero".equals(userType)) {
                    out.write("{\"status\":\"success\",\"redirect\":\"HomeEnfermero.html\"}");
                } else if ("Doctor".equals(userType)) {
                    out.write("{\"status\":\"success\",\"redirect\":\"HomeDoctor.html\"}");
                }else if ("Recepcionista".equals(userType)) {
                    out.write("{\"status\":\"success\",\"redirect\":\"HomeRecepcionista.html\"}");
                }else if ("Farmaceutico".equals(userType)) {
                    out.write("{\"status\":\"success\",\"redirect\":\"HomeFarmaceutico.html\"}");
                }
            } else {
                out.write("{\"status\":\"error\",\"message\":\"Las credenciales no son validas.\"}");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\",\"message\":\"Database connection error.\"}");
        }
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
        return "Login Servlet";
    }// </editor-fold>

}