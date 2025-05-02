import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/RecruiterResetPasswordServlet")
public class RecruiterResetPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // Retrieve new password from request
        String newPassword = request.getParameter("new_password");

        // Get the session to retrieve recruiter ID
        HttpSession session = request.getSession();
        Integer recruiterId = (Integer) session.getAttribute("resetRecruiterId");

        // If session ID is null, redirect to forgot password page
        if (recruiterId == null) {
            response.sendRedirect("recruiter-forgot-password.html?error=Session Expired. Try Again.");
            return;
        }

        try (Connection con = DBUtils.getConnection()) {
            // Update password in database
            String query = "UPDATE recruiters SET password = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newPassword); // You can hash the password before storing it for security
            ps.setInt(2, recruiterId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                // Password updated successfully, remove session attribute
                session.removeAttribute("resetRecruiterId");

                // Send JavaScript alert and redirect to login page
                response.setContentType("text/html");
                response.getWriter().println("<script>alert('Password changed successfully!');"
                        + "window.location='recruiter_login.html';</script>");
            } else {
                response.sendRedirect("recruiters-reset-password.html?error=Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("recruiters-reset-password.html?error=Database Error.");
        }
    }
}
