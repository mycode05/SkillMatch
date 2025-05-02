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

@WebServlet("/RecruiterProfileServlet")
public class RecruiterProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get the session without creating a new one
        HttpSession session = request.getSession(false);

        // Debugging log for session checking
        System.out.println("🔍 Checking session...");
        if (session == null) {
            System.out.println("⚠️ No active session. Redirecting to login.");
            response.sendRedirect("RecruiterLoginServlet");
            return;
        }

        // Retrieve recruiterEmail from session
        String recruiterEmail = (String) session.getAttribute("recruiterEmail");
        if (recruiterEmail == null) {
            System.out.println("⚠️ No recruiterEmail in session. Redirecting to login.");
            response.sendRedirect("RecruiterLoginServlet");
            return;
        }

        System.out.println("✅ Recruiter session found. Email: " + recruiterEmail);

        // Database Query
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT company_name, email, mobile, address FROM recruiters WHERE email = ?")) {

            stmt.setString(1, recruiterEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String companyName = rs.getString("company_name");
                String email = rs.getString("email");
                String mobile = rs.getString("mobile");
                String address = rs.getString("address");

                System.out.println("✅ Recruiter found in DB: " + companyName);

                // Generate HTML response dynamically
                response.setContentType("text/html");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                    "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "    <meta charset='UTF-8'>" +
                    "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "    <title>Recruiter Profile | SkillMatch</title>" +
                    "    <link rel='stylesheet' href='profile.css'>" +
                    "    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>" +
                    "    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap' rel='stylesheet'>" +
                    "</head>" +
                    "<body>" +
                    "    <div class='profile-container'>" +
                    "        <div class='profile-banner'></div>" +
                    "        <div class='profile-info'>" +
                    "            <h2>" + companyName + "</h2>" +
                    "            <p><i class='fas fa-envelope'></i> " + email + "</p>" +
                    "            <p><i class='fas fa-phone'></i> " + mobile + "</p>" +
                    "            <p><i class='fas fa-map-marker-alt'></i> " + address + "</p>" +
                    "        </div>" +
                    "        <div class='button-group'>" +
                    "            <a href='edit_profile.html' class='btn primary-btn'><i class='fas fa-edit'></i> Edit Profile</a>" +
                    "            <a href='change_password.html' class='btn secondary-btn'><i class='fas fa-key'></i> Change Password</a>" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>"
                );
            } else {
                System.out.println("⚠️ Recruiter not found in DB for email: " + recruiterEmail);
                response.getWriter().write("<h2>Error: Recruiter not found in the database</h2>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<h2>Error: Database connection issue</h2>");
        }
    }
}
