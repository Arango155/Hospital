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

@WebServlet("/GestionPacientesServlet")
public class GestionPacientesServlet extends HttpServlet {

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
                    mostrarPacientes(out, connection);
                } else if ("editar".equals(action)) {
                    editarPaciente(request, out, connection);
                } else if ("borrar".equals(action)) {
                    borrarPaciente(request, out, connection);
                } else if ("obtener".equals(action)) {
                    obtenerPaciente(request, out, connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"message\":\"Error de conexión a la base de datos.\"}");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void mostrarPacientes(PrintWriter out, Connection connection) throws SQLException {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        String sql = "SELECT * FROM PACIENTES";
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
                           .append("\"NOMBRE\":\"").append(resultSet.getString("NOMBRE")).append("\",")
                           .append("\"EDAD\":\"").append(resultSet.getInt("EDAD")).append("\",")
                           .append("\"GENERO\":\"").append(resultSet.getString("GENERO")).append("\",")
                           .append("\"FECHA_INGRESO\":\"").append(resultSet.getDate("FECHA_INGRESO")).append("\"")
                           .append("}");
            }
        }
        jsonBuilder.append("]");
        out.write(jsonBuilder.toString());
    }

    private void editarPaciente(HttpServletRequest request, PrintWriter out, Connection connection) {
        String dpi = request.getParameter("DPI");
        String nombre = request.getParameter("NOMBRE");
        int edad = Integer.parseInt(request.getParameter("EDAD"));
        String genero = request.getParameter("GENERO");
        String fechaIngreso = request.getParameter("FECHA_INGRESO");

        String sql = "UPDATE PACIENTES SET NOMBRE = ?, EDAD = ?, GENERO = ?, FECHA_INGRESO = ? WHERE DPI = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setInt(2, edad);
            statement.setString(3, genero);
            statement.setDate(4, java.sql.Date.valueOf(fechaIngreso));
            statement.setString(5, dpi);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Paciente actualizado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el paciente o no se pudo actualizar.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\",\"message\":\"Error al actualizar el paciente en la base de datos.\"}");
        }
    }

    private void borrarPaciente(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String dpi = request.getParameter("DPI");

        String sql = "DELETE FROM PACIENTES WHERE DPI = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dpi);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                out.write("{\"status\":\"success\",\"message\":\"Paciente borrado correctamente.\"}");
            } else {
                out.write("{\"status\":\"error\",\"message\":\"No se encontró el paciente.\"}");
            }
        }
    }

    private void obtenerPaciente(HttpServletRequest request, PrintWriter out, Connection connection) throws SQLException {
        String dpi = request.getParameter("dpi");

        String sql = "SELECT * FROM PACIENTES WHERE DPI = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dpi);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    out.write("{");
                    out.write("\"DPI\":\"" + resultSet.getString("DPI") + "\",");
                    out.write("\"NOMBRE\":\"" + resultSet.getString("NOMBRE") + "\",");
                    out.write("\"EDAD\":\"" + resultSet.getInt("EDAD") + "\",");
                    out.write("\"GENERO\":\"" + resultSet.getString("GENERO") + "\",");
                    out.write("\"FECHA_INGRESO\":\"" + resultSet.getDate("FECHA_INGRESO") + "\"");
                    out.write("}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"No se encontró el paciente.\"}");
                }
            }
        }
    }
}
