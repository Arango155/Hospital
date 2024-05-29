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

@WebServlet("/GestionPrescripcionesServlet")
public class GestionPrescripcionesServlet extends HttpServlet {

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
                    mostrarPrescripciones(out, connection);
                } else if ("editar".equals(action)) {
                    editarPrescripcion(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarPrescripcion(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerPrescripcion(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarPrescripciones(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM PRESCRIPCIONES";
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
                           .append("\"PRESCRIPCIONID\":\"").append(resultSet.getString("PRESCRIPCIONID")).append("\",")
                           .append("\"DPI\":\"").append(resultSet.getString("DPI")).append("\",")
                           .append("\"MEDICAMENTOID\":\"").append(resultSet.getString("MEDICAMENTOID")).append("\",")
                           .append("\"CANTIDAD\":\"").append(resultSet.getString("CANTIDAD")).append("\",")
                           .append("\"FECHA\":\"").append(resultSet.getString("FECHA")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

 private void editarPrescripcion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
    String prescripcionId = request.getParameter("PRESCRIPCIONID");
    String dpi = request.getParameter("DPI");
    int medicamentoId = Integer.parseInt(request.getParameter("MEDICAMENTOID"));
    int cantidad = Integer.parseInt(request.getParameter("CANTIDAD"));

    // Use a PreparedStatement without the FECHA column in the update statement
    String sql = "UPDATE PRESCRIPCIONES SET DPI = ?, MEDICAMENTOID = ?, CANTIDAD = ? WHERE PRESCRIPCIONID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, dpi);
        statement.setInt(2, medicamentoId);
        statement.setInt(3, cantidad);
        statement.setString(4, prescripcionId);
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            out.write("{\"status\":\"success\",\"message\":\"Prescripción actualizada correctamente.\"}");
        } else {
            out.write("{\"status\":\"error\",\"message\":\"No se encontró la prescripción.\"}");
        }
    }
}


    private void borrarPrescripcion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String prescripcionId = request.getParameter("PRESCRIPCIONID");

        String sql = "DELETE FROM PRESCRIPCIONES WHERE PRESCRIPCIONID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, prescripcionId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Prescripción borrada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la prescripción.\"}");
            }
        }
    }

    private void obtenerPrescripcion(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String prescripcionId = request.getParameter("prescriptionId");

        String sql = "SELECT * FROM PRESCRIPCIONES WHERE PRESCRIPCIONID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, prescripcionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"PRESCRIPCIONID\":\"" + resultSet.getString("PRESCRIPCIONID") + "\",");
                    out.write("\"DPI\":\"" + resultSet.getString("DPI") + "\",");
                    out.write("\"MEDICAMENTOID\":\"" + resultSet.getString("MEDICAMENTOID") + "\",");
                    out.write("\"CANTIDAD\":\"" + resultSet.getString("CANTIDAD") + "\",");
                    out.write("\"FECHA\":\"" + resultSet.getString("FECHA") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la prescripción.\"}");
                }
            }
        }
    }
}
