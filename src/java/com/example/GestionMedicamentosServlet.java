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

@WebServlet("/GestionMedicamentosServlet")
public class GestionMedicamentosServlet extends HttpServlet {

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
                    mostrarMedicamentos(out, connection);
                } else if ("editar".equals(action)) {
                    editarMedicamento(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarMedicamento(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerMedicamento(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarMedicamentos(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM MEDICAMENTOS";
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
                           .append("\"MEDICAMENTOID\":\"").append(resultSet.getString("MEDICAMENTOID")).append("\",")
                           .append("\"NOMBRE\":\"").append(resultSet.getString("NOMBRE")).append("\",")
                           .append("\"DESCRIPCION\":\"").append(resultSet.getString("DESCRIPCION")).append("\",")
                           .append("\"PRECIO\":\"").append(resultSet.getString("PRECIO")).append("\",")
                           .append("\"CANTIDAD\":\"").append(resultSet.getString("CANTIDAD")).append("\",")
                           .append("\"CANTIDADMINIMA\":\"").append(resultSet.getString("CANTIDADMINIMA")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarMedicamento(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String medicamentoId = request.getParameter("MEDICAMENTOID");
        String nombre = request.getParameter("NOMBRE");
        String descripcion = request.getParameter("DESCRIPCION");
        double precio = Double.parseDouble(request.getParameter("PRECIO"));
        int cantidad = Integer.parseInt(request.getParameter("CANTIDAD"));
        int cantidadMinima = Integer.parseInt(request.getParameter("CANTIDADMINIMA"));

        String sql = "UPDATE MEDICAMENTOS SET NOMBRE = ?, DESCRIPCION = ?, PRECIO = ?, CANTIDAD = ?, CANTIDADMINIMA = ? WHERE MEDICAMENTOID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setString(2, descripcion);
            statement.setDouble(3, precio);
            statement.setInt(4, cantidad);
            statement.setInt(5, cantidadMinima);
            statement.setString(6, medicamentoId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Medicamento actualizado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el medicamento.\"}");
            }
        }
    }

    private void borrarMedicamento(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String medicamentoId = request.getParameter("MEDICAMENTOID");

        String sql = "DELETE FROM MEDICAMENTOS WHERE MEDICAMENTOID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, medicamentoId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Medicamento borrado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el medicamento.\"}");
            }
        }
    }

    private void obtenerMedicamento(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String medicamentoId = request.getParameter("medicamentoId");

        String sql = "SELECT * FROM MEDICAMENTOS WHERE MEDICAMENTOID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, medicamentoId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"MEDICAMENTOID\":\"" + resultSet.getString("MEDICAMENTOID") + "\",");
                    out.write("\"NOMBRE\":\"" + resultSet.getString("NOMBRE") + "\",");
                    out.write("\"DESCRIPCION\":\"" + resultSet.getString("DESCRIPCION") + "\",");
                    out.write("\"PRECIO\":\"" + resultSet.getString("PRECIO") + "\",");
                    out.write("\"CANTIDAD\":\"" + resultSet.getString("CANTIDAD") + "\",");
                    out.write("\"CANTIDADMINIMA\":\"" + resultSet.getString("CANTIDADMINIMA") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró el medicamento.\"}");
                }
            }
        }
    }
}
