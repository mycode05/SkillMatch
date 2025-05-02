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

@WebServlet("/RecruiterForgotPasswordServlet")
public class RecruiterForgotPasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String mobile = request.getParameter("mobile");

        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT id FROM recruiters WHERE email = ? AND mobile = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, mobile);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Store recruiter ID in session for password reset verification
                HttpSession session = request.getSession();
                session.setAttribute("resetRecruiterId", rs.getInt("id"));

                // Redirect to reset password page
                response.sendRedirect("recruiters-reset-password.html");
            } else {
                // Redirect back with error message
                response.sendRedirect("recruiter-forgot-password.html?error=Invalid Email or Mobile Number");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("recruiter-forgot-password.html?error=Database Error");
        }
    }
}
