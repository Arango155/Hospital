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

@WebServlet("/GestionDesasignacionesServlet")
public class GestionDesasignacionesServlet extends HttpServlet {

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
                    mostrarDesasignaciones(out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerDesasignacion(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarDesasignaciones(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM REGISTRO_DESASIGNACIONES_DOCTOR";
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
                           .append("\"DPI\":\"").append(resultSet.getString("DPI")).append("\",")
                           .append("\"DOCTORID\":\"").append(resultSet.getString("DOCTORID")).append("\",")
                           .append("\"FECHA_DESASIGNACION\":\"").append(resultSet.getString("FECHA_DESASIGNACION")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void obtenerDesasignacion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String dpi = request.getParameter("dpi");

        String sql = "SELECT * FROM REGISTRO_DESASIGNACIONES_DOCTOR WHERE DPI = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dpi);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"DPI\":\"" + resultSet.getString("DPI") + "\",");
                    out.write("\"DOCTORID\":\"" + resultSet.getString("DOCTORID") + "\",");
                    out.write("\"FECHA_DESASIGNACION\":\"" + resultSet.getString("FECHA_DESASIGNACION") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la desasignación.\"}");
                }
            }
        }
    }
}
