import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/StudentRegisterServlet")
public class StudentRegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");
        String dob = request.getParameter("dob");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            response.setContentType("text/html");
            response.getWriter().println("<h1>Passwords do not match.</h1>");
            response.getWriter().println("<a href='register.html'>Try Again</a>");
            return;
        }

        try (Connection conn = DBUtils.getConnection()) {
            String sql = "INSERT INTO users (username, password, email, mobile, dob, address, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // In a real app, hash the password
                stmt.setString(3, email);
                stmt.setString(4, mobile);
                stmt.setString(5, dob);
                stmt.setString(6, address);
                stmt.setString(7, gender);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Redirect to the success page
                    response.sendRedirect("student_successfully-registered.html");
                } else {
                    response.setContentType("text/html");
                    response.getWriter().println("<h1>Registration failed.</h1>");
                    response.getWriter().println("<a href='student_register.html'>Try Again</a>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h1>Database error: " + e.getMessage() + "</h1>");
            response.getWriter().println("<a href='student_register.html'>Try Again</a>");
        }
    }
}