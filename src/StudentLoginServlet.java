import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/StudentLoginServlet")
public class StudentLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DBUtils.getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password); // In a real app, use password hashing (e.g., bcrypt)
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Successful login
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getInt("id")); // Assuming 'id' is the user ID column
                    session.setAttribute("username", rs.getString("username"));
                    response.sendRedirect("Studentwelcome"); // Redirect to welcome page
                } else {
                    // Failed login
                    response.setContentType("text/html");
                    response.getWriter().println("<h1>Invalid email or password.</h1>");
                    response.getWriter().println("<a href='student_login.html'>Try Again</a>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h1>Database error: " + e.getMessage() + "</h1>");
        }
    }
}