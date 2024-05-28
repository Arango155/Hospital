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

@WebServlet(name = "RegistrarDoctorServlet", urlPatterns = {"/RegistrarDoctorServlet"})
public class RegistrarDoctorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener parámetros del formulario
        String DOCTORID = request.getParameter("DOCTORID");
        String NOMBRE = request.getParameter("NOMBRE");
        String ESPECIALIDADID = request.getParameter("ESPECIALIDADID");
        
        // Establecer conexión a la base de datos Oracle
        try {
            String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
            String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Reemplaza con tu URL de conexión
            String usuario = "HOSPITAL"; // Reemplaza con tu usuario de Oracle
            String contraseña = "1234"; // Reemplaza con tu contraseña de Oracle
            
            Class.forName(jdbcDriver);
            Connection conexion = DriverManager.getConnection(jdbcUrl, usuario, contraseña);
            
            // Preparar la sentencia SQL para la inserción
            String sql = "INSERT INTO DOCTORES (DOCTORID, NOMBRE, ESPECIALIDADID) VALUES (?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, DOCTORID);
            statement.setString(2, NOMBRE);
            statement.setString(3, ESPECIALIDADID);
            
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Envía una respuesta de éxito al cliente
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.print("success");
            } else {
                // Envía una respuesta de error al cliente
                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();
                out.print("error");
            }

            statement.close();
            conexion.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.print("error");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
