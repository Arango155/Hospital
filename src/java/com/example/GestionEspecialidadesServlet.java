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

@WebServlet("/GestionEspecialidadesServlet")
public class GestionEspecialidadesServlet extends HttpServlet {

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
                    mostrarEspecialidades(out, connection);
                } else if ("editar".equals(action)) {
                    editarEspecialidad(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarEspecialidad(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerEspecialidad(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarEspecialidades(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM ESPECIALIDADES";
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
                           .append("\"ESPECIALIDADID\":\"").append(resultSet.getString("ESPECIALIDADID")).append("\",")
                           .append("\"NOMBRE\":\"").append(resultSet.getString("NOMBRE")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarEspecialidad(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String especialidadId = request.getParameter("ESPECIALIDADID");
        String nombre = request.getParameter("NOMBRE");

        String sql = "UPDATE ESPECIALIDADES SET NOMBRE = ? WHERE ESPECIALIDADID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setString(2, especialidadId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Especialidad actualizada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la especialidad.\"}");
            }
        }
    }

    private void borrarEspecialidad(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String especialidadId = request.getParameter("ESPECIALIDADID");

        String sql = "DELETE FROM ESPECIALIDADES WHERE ESPECIALIDADID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, especialidadId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Especialidad borrada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la especialidad.\"}");
            }
        }
    }

    private void obtenerEspecialidad(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String especialidadId = request.getParameter("especialidadId");

        String sql = "SELECT * FROM ESPECIALIDADES WHERE ESPECIALIDADID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, especialidadId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{\"ESPECIALIDADID\":\"" + resultSet.getString("ESPECIALIDADID") + "\","
                            + "\"NOMBRE\":\"" + resultSet.getString("NOMBRE") + "\"}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la especialidad.\"}");
                }
            }
        }
    }
}
