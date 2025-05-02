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

@WebServlet("/RecruiterLoginServlet")
public class RecruiterLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DBUtils.getConnection()) {
            String sql = "SELECT id, company_name FROM recruiters WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int recruiterId = rs.getInt("id");
                    String companyName = rs.getString("company_name");

                    // Create a session and store recruiter details
                    HttpSession session = request.getSession(true);
                    session.setAttribute("recruiterId", recruiterId);
                    session.setAttribute("companyName", companyName); // Store company name
                    session.setMaxInactiveInterval(60 * 60); // 1 hour session timeout
                    
                    System.out.println("Login successful for recruiter ID: " + recruiterId);
                    response.sendRedirect("RecruiterWelcomeServlet"); // Redirect to dashboard
                } else {
                    System.out.println("Invalid email or password.");
                    response.setContentType("text/html");
                    response.getWriter().println("<h1>Invalid email or password.</h1>");
                    response.getWriter().println("<a href='recruiter_login.html'>Try Again</a>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h1>Database error: " + e.getMessage() + "</h1>");
        }
    }
}
