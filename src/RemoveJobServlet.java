import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/RemoveJobServlet")
public class RemoveJobServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Retrieve session and recruiter ID
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("recruiterId") == null) {
            System.out.println("⚠️ Session expired! Redirecting to login.");
            response.sendRedirect("recruiter_login.html");
            return;
        }

        session.setMaxInactiveInterval(60 * 60); // Keep session active
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        System.out.println("✅ Recruiter ID from session: " + recruiterId);

        // ✅ Get Job ID from form and validate
        String jobIdStr = request.getParameter("jobId");
        if (jobIdStr == null || !jobIdStr.matches("\\d+")) {
            System.out.println("⚠️ Invalid Job ID: " + jobIdStr);
            response.getWriter().println("<script>"
                + "alert('⚠️ Invalid Job ID. Please enter a valid number.');"
                + "window.location.href='recruiter_remove_job.html';"
                + "</script>");
            return;
        }

        int jobId = Integer.parseInt(jobIdStr);
        System.out.println("✅ Job ID received from form: " + jobId);

        // ✅ Delete job from database
        try (Connection conn = DBUtils.getConnection()) {
            if (conn == null) {
                System.out.println("❌ Database connection failed!");
                response.getWriter().println("<script>alert('❌ Database connection failed!');</script>");
                return;
            }
            System.out.println("✅ Database connection successful.");

            String sql = "DELETE FROM jobs WHERE job_id = ? AND recruiter_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, jobId);
                stmt.setInt(2, recruiterId);

                System.out.println("Executing SQL: " + sql);
                System.out.println("With Parameters: Job ID = " + jobId + ", Recruiter ID = " + recruiterId);

                int rowsDeleted = stmt.executeUpdate();
                System.out.println("Rows deleted: " + rowsDeleted);

                if (rowsDeleted > 0) {
                    System.out.println("✅ Job ID " + jobId + " successfully removed.");
                    response.getWriter().println("<script>"
                        + "alert('✅ Job removed successfully!');"
                        + "window.location.href='recruiter_user.html';"
                        + "</script>");
                } else {
                    System.out.println("⚠️ No matching job found for deletion.");
                    response.getWriter().println("<script>"
                        + "alert('⚠️ Error: Job ID not found or unauthorized!');"
                        + "window.location.href='recruiter_remove_job.html';"
                        + "</script>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Database error: " + e.getMessage());
            response.getWriter().println("<script>"
                + "alert('❌ Database error: " + e.getMessage() + "');"
                + "window.location.href='recruiter_remove_job.html';"
                + "</script>");
        }
    }
}
