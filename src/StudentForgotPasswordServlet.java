import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StudentForgotPasswordServlet")
public class StudentForgotPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String dob = request.getParameter("dob"); // Format should match the DB

        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT id FROM users WHERE username = ? AND dob = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, dob);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Create session and store user ID for reset password verification
                HttpSession session = request.getSession();
                session.setAttribute("resetUserId", rs.getInt("id"));

                // Redirect to reset password page
                response.sendRedirect("reset-password.html");
            } else {
                // Redirect back with error message
                response.sendRedirect("forgot-password.html?error=Invalid Username or Date of Birth");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("forgot-password.html?error=Database Error");
        }
    }
}
