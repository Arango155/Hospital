/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author blanc
 */
@WebServlet(name = "RegistrarEspecialidadServlet", urlPatterns = {"/RegistrarEspecialidadServlet"})
public class RegistrarEspecialidadServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final long serialVersionUID = 1L;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        // Obtener parámetros del formulario
        String ESPECIALIDADID = request.getParameter("ESPECIALIDADID");
        String NOMBRE = request.getParameter("NOMBRE");
        
        
        // Establecer conexión a la base de datos Oracle
        try{
        String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
        String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Reemplaza con tu URL de conexión
        String usuario = "HOSPITAL"; // Reemplaza con tu usuario de Oracle
        String contraseña = "1234"; // Reemplaza con tu contraseña de Oracle
        
        Class.forName(jdbcDriver);
        Connection conexion = DriverManager.getConnection(jdbcUrl, usuario, contraseña);
            
            // Preparar la sentencia SQL para la inserción
             String sql = "INSERT INTO ESPECIALIDADES (ESPECIALIDADID, NOMBRE) VALUES (?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, ESPECIALIDADID);
            statement.setString(2, NOMBRE);
           
           
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Registration successful
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>¡Registro exitoso!</h3>");
                out.println("<p>Los datos del cliente se han registrado correctamente en la base de datos.</p>");
                out.println("<a href=\"index.html\">Regresar al formulario</a>");
                out.println("</body></html>");
            } else {
                // Registration failed
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al registrar cliente.");
            }

            statement.close();
            conexion.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de conexión a la base de datos.");
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
        return "Short description";
    }// </editor-fold>

}
