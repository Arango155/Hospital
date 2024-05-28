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

@WebServlet("/GestionCamasServlet")
public class GestionCamasServlet extends HttpServlet {

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
                    mostrarCamas(out, connection);
                } else if ("editar".equals(action)) {
                    editarCama(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarCama(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerCama(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarCamas(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM CAMAS";
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
                           .append("\"CAMAID\":\"").append(resultSet.getString("CAMAID")).append("\",")
                           .append("\"HABITACIONID\":\"").append(resultSet.getString("HABITACIONID")).append("\",")
                           .append("\"NUMEROCAMA\":\"").append(resultSet.getString("NUMEROCAMA")).append("\",")
                           .append("\"ESTADO\":\"").append(resultSet.getString("ESTADO")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarCama(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String camaId = request.getParameter("CAMAID");
        String habitacionId = request.getParameter("HABITACIONID");
        String numeroCama = request.getParameter("NUMEROCAMA");
        String estado = request.getParameter("ESTADO");

        String sql = "UPDATE CAMAS SET HABITACIONID = ?, NUMEROCAMA = ?, ESTADO = ? WHERE CAMAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, habitacionId);
            statement.setString(2, numeroCama);
            statement.setString(3, estado);
            statement.setString(4, camaId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Cama actualizada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la cama.\"}");
            }
        }
    }

    private void borrarCama(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String camaId = request.getParameter("CAMAID");

        String sql = "DELETE FROM CAMAS WHERE CAMAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, camaId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Cama borrada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la cama.\"}");
            }
        }
    }

    private void obtenerCama(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String camaId = request.getParameter("camaId");

        String sql = "SELECT * FROM CAMAS WHERE CAMAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, camaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"CAMAID\":\"" + resultSet.getString("CAMAID") + "\",");
                    out.write("\"HABITACIONID\":\"" + resultSet.getString("HABITACIONID") + "\",");
                    out.write("\"NUMEROCAMA\":\"" + resultSet.getString("NUMEROCAMA") + "\",");
                    out.write("\"ESTADO\":\"" + resultSet.getString("ESTADO") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la cama.\"}");
                }
            }
        }
    }
}
