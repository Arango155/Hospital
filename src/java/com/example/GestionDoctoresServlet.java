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

@WebServlet("/GestionDoctoresServlet")
public class GestionDoctoresServlet extends HttpServlet {

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
                    mostrarDoctores(out, connection);
                } else if ("editar".equals(action)) {
                    editarDoctor(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarDoctor(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerDoctor(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarDoctores(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM DOCTORES";
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
                           .append("\"DOCTORID\":\"").append(resultSet.getString("DOCTORID")).append("\",")
                           .append("\"NOMBRE\":\"").append(resultSet.getString("NOMBRE")).append("\",")
                           .append("\"ESPECIALIDADID\":\"").append(resultSet.getString("ESPECIALIDADID")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarDoctor(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String doctorId = request.getParameter("DOCTORID");
        String nombre = request.getParameter("NOMBRE");
        int especialidadId = Integer.parseInt(request.getParameter("ESPECIALIDADID"));

        String sql = "UPDATE DOCTORES SET NOMBRE = ?, ESPECIALIDADID = ? WHERE DOCTORID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setInt(2, especialidadId);
            statement.setString(3, doctorId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Doctor actualizado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el doctor.\"}");
            }
        }
    }

    private void borrarDoctor(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String doctorId = request.getParameter("DOCTORID");

        String sql = "DELETE FROM DOCTORES WHERE DOCTORID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, doctorId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Doctor borrado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el doctor.\"}");
            }
        }
    }

    private void obtenerDoctor(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String doctorId = request.getParameter("doctorId");

        String sql = "SELECT * FROM DOCTORES WHERE DOCTORID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, doctorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"DOCTORID\":\"" + resultSet.getString("DOCTORID") + "\",");
                    out.write("\"NOMBRE\":\"" + resultSet.getString("NOMBRE") + "\",");
                    out.write("\"ESPECIALIDADID\":\"" + resultSet.getString("ESPECIALIDADID") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró el doctor.\"}");
                }
            }
        }
    }
}
