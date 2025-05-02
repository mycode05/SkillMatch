import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/RecruiterRegisterServlet")
public class RecruiterRegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve form data
        String companyName = request.getParameter("company-name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        String mobile = request.getParameter("mobile");
        String address = request.getParameter("address");

        // Validate password match
        if (!password.equals(confirmPassword)) {
            response.setContentType("text/html");
            response.getWriter().println("<h1>Passwords do not match. Please try again.</h1>");
            response.getWriter().println("<a href='recruiter_register.html'>Go Back</a>");
            return;
        }

        // Insert into database
        try (Connection conn = DBUtils.getConnection()) {
            String sql = "INSERT INTO recruiters (company_name, email, password, mobile, address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, companyName);
                stmt.setString(2, email);
                stmt.setString(3, password); // In a real app, hash the password (e.g., bcrypt)
                stmt.setString(4, mobile);
                stmt.setString(5, address);
                stmt.executeUpdate();
            }
            // Redirect to success page
            response.sendRedirect("recruiter_successfully-registered.html");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h1>Registration failed: " + e.getMessage() + "</h1>");
            response.getWriter().println("<a href='recruiter_register.html'>Go Back</a>");
        }
    }
}