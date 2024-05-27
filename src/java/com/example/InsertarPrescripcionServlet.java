import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.CallableStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/InsertarPrescripcionServlet")
public class InsertarPrescripcionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtener parámetros del formulario
        int prescripcionID = Integer.parseInt(request.getParameter("prescripcionID"));
        int dpi = Integer.parseInt(request.getParameter("dpi"));
        int medicamentoID = Integer.parseInt(request.getParameter("medicamentoID"));
        int cantidad = Integer.parseInt(request.getParameter("cantidad"));
        String fecha = request.getParameter("fecha");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Conectar a la base de datos y llamar al procedimiento almacenado
        try {
            String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
            String dbUrl = "jdbc:oracle:thin:@localhost:1521:xe";
            String dbUser = "HOSPITAL";
            String dbPassword = "1234";

            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            String sql = "{call Insertar_Prescripcion(?, ?, ?, ?, ?)}";
            CallableStatement statement = connection.prepareCall(sql);
            statement.setInt(1, prescripcionID);
            statement.setInt(2, dpi);
            statement.setInt(3, medicamentoID);
            statement.setInt(4, cantidad);
            statement.setString(5, fecha);

            statement.execute();
            statement.close();
            connection.close();

            out.write("{\"status\":\"success\"}");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\",\"message\":\"Error al insertar la prescripción: " + e.getMessage() + "\"}");
        }
    }
}