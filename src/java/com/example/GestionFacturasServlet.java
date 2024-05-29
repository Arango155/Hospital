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

@WebServlet("/GestionFacturasServlet")
public class GestionFacturasServlet extends HttpServlet {

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
                    mostrarFacturas(out, connection);
                } else if ("editar".equals(action)) {
                    editarFactura(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarFactura(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerFactura(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarFacturas(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM FACTURAS";
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
                        .append("\"FACTURAID\":\"").append(resultSet.getString("FACTURAID")).append("\",")
                        .append("\"DPI\":\"").append(resultSet.getString("DPI")).append("\",")
                        .append("\"MONTO\":\"").append(resultSet.getString("MONTO")).append("\",")
                        .append("\"FECHAEMISION\":\"").append(resultSet.getString("FECHAEMISION")).append("\",")
                        .append("\"ESTADO\":\"").append(resultSet.getString("ESTADO")).append("\"")
                        .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarFactura(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String facturaId = request.getParameter("FACTURAID");
        String dpi = request.getParameter("DPI");
        double monto = Double.parseDouble(request.getParameter("MONTO"));
        String fechaEmision = request.getParameter("FECHAEMISION");
        String estado = request.getParameter("ESTADO");

        String sql = "UPDATE FACTURAS SET DPI = ?, MONTO = ?, FECHAEMISION = ?, ESTADO = ? WHERE FACTURAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dpi);
            statement.setDouble(2, monto);
            statement.setString(3, fechaEmision);
            statement.setString(4, estado);
            statement.setString(5, facturaId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Factura actualizada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la factura.\"}");
            }
        }
    }

    private void borrarFactura(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String facturaId = request.getParameter("FACTURAID");

        String sql = "DELETE FROM FACTURAS WHERE FACTURAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, facturaId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Factura borrada correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró la factura.\"}");
            }
        }
    }

    private void obtenerFactura(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String facturaId = request.getParameter("facturaId");

        String sql = "SELECT * FROM FACTURAS WHERE FACTURAID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, facturaId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"FACTURAID\":\"" + resultSet.getString("FACTURAID") + "\",");
                    out.write("\"DPI\":\"" + resultSet.getString("DPI") + "\",");
                    out.write("\"MONTO\":\"" + resultSet.getString("MONTO") + "\",");
                    out.write("\"FECHAEMISION\":\"" + resultSet.getString("FECHAEMISION") + "\",");
                    out.write("\"ESTADO\":\"" + resultSet.getString("ESTADO") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró la factura.\"}");
                }
            }
        }
    }
}
