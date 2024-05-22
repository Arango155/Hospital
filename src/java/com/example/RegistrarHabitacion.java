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

@WebServlet(name = "RegistrarHabitacion", urlPatterns = {"/RegistrarHabitacion"})
public class RegistrarHabitacion extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros del formulario
        String HABITACIONID = request.getParameter("HABITACIONID");
        String NUMEROHABITACION = request.getParameter("NUMEROHABITACION");
        String TIPO = request.getParameter("TIPO");
        String ESTADO = request.getParameter("ESTADO");

        // Establecer conexión a la base de datos Oracle
        try {
            String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
            String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:xe"; // Reemplaza con tu URL de conexión
            String usuario = "c##hospital"; // Reemplaza con tu usuario de Oracle
            String contraseña = "1234"; // Reemplaza con tu contraseña de Oracle

            Class.forName(jdbcDriver);
            Connection conexion = DriverManager.getConnection(jdbcUrl, usuario, contraseña);

            // Preparar la sentencia SQL para la inserción
            String sql = "INSERT INTO HABITACIONES (HABITACIONID, NUMEROHABITACION, TIPO, ESTADO) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conexion.prepareStatement(sql);
            statement.setString(1, HABITACIONID);
            statement.setString(2, NUMEROHABITACION);
            statement.setString(3, TIPO);
            statement.setString(4, ESTADO);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                // Registro exitoso
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h3>¡Registro exitoso!</h3>");
                out.println("<p>La habitación se ha registrado correctamente en la base de datos.</p>");
                out.println("<a href=\"index.html\">Regresar al formulario</a>");
                out.println("</body></html>");
            } else {
                // Registro fallido
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al registrar habitación.");
            }

            statement.close();
            conexion.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error de conexión a la base de datos.");
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
        return "Servlet para registrar habitaciones en la base de datos.";
    }
}