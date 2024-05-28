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

@WebServlet("/GestionHabitacionesServlet")
public class GestionHabitacionesServlet extends HttpServlet {

    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String DB_USER = "HOSPITAL";
    private static final String DB_PASSWORD = "1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Class.forName(JDBC_DRIVER);
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if ("mostrar".equals(action)) {
                    mostrarHabitaciones(out, connection);
                } else if ("editar".equals(action)) {
                    editarHabitacion(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarHabitacion(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerHabitacion(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarHabitaciones(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM HABITACIONES";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonBuilder.append(",");
                } else {
                    first = false;
                }
                jsonBuilder.append("{")
                           .append("\"HABITACIONID\":\"").append(resultSet.getString("HABITACIONID")).append("\",")
                           .append("\"NUMEROHABITACION\":\"").append(resultSet.getString("NUMEROHABITACION")).append("\",")
                           .append("\"TIPO\":\"").append(resultSet.getString("TIPO")).append("\",")
                           .append("\"ESTADO\":\"").append(resultSet.getString("ESTADO")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarHabitacion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String habitacionId = request.getParameter("HABITACIONID");
        String numeroHabitacion = request.getParameter("NUMEROHABITACION");
        String tipo = request.getParameter("TIPO");
        String estado = request.getParameter("ESTADO");

        String sql = "UPDATE HABITACIONES SET NUMEROHABITACION = ?, TIPO = ?, ESTADO = ? WHERE HABITACIONID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, numeroHabitacion);
            statement.setString(2, tipo);
            statement.setString(3, estado);
            statement.setString(4, habitacionId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Habitación actualizada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la habitación.\"}");
            }
        }
    }

    private void borrarHabitacion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String habitacionId = request.getParameter("HABITACIONID");

        String sql = "DELETE FROM HABITACIONES WHERE HABITACIONID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, habitacionId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Habitación borrada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la habitación.\"}");
            }
        }
    }

    private void obtenerHabitacion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String habitacionId = request.getParameter("habitacionId");

        String sql = "SELECT * FROM HABITACIONES WHERE HABITACIONID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, habitacionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"HABITACIONID\":\"" + resultSet.getString("HABITACIONID") + "\",");
                    out.write("\"NUMEROHABITACION\":\"" + resultSet.getString("NUMEROHABITACION") + "\",");
                    out.write("\"TIPO\":\"" + resultSet.getString("TIPO") + "\",");
                    out.write("\"ESTADO\":\"" + resultSet.getString("ESTADO") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la habitación.\"}");
                }
            }
        }
    }
}
