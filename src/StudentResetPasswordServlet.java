import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StudentResetPasswordServlet")
public class StudentResetPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if session exists and contains resetUserId
        if (session == null || session.getAttribute("resetUserId") == null) {
            response.sendRedirect("forgot-password.html?error=Session expired. Try again.");
            return;
        }

        int userId = (int) session.getAttribute("resetUserId");
        String newPassword = request.getParameter("new_password");

        try (Connection con = DBUtils.getConnection()) {
            String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(updateQuery);
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                // Invalidate session after successful password reset
                session.invalidate();

                // Send JavaScript alert before redirecting
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Password reset successfully. Please login.'); window.location.href='student_login.html';</script>");
            } else {
                response.sendRedirect("reset-password.html?error=Error resetting password. Try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("reset-password.html?error=Database Error");
        }
    }
}
