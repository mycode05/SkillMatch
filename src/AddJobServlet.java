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

@WebServlet("/AddJobServlet")
public class AddJobServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get session to retrieve recruiter ID
        HttpSession session = request.getSession(false);
        Integer recruiterId = (session != null) ? (Integer) session.getAttribute("recruiterId") : null;

        if (recruiterId == null) {
            response.sendRedirect("recruiter_login.html"); // Redirect to login if not authenticated
            return;
        }

        // Retrieve job details from the form
        String jobTitle = request.getParameter("jobTitle");
        String companyName = request.getParameter("companyName");
        String location = request.getParameter("location");
        String salary = request.getParameter("salary");
        String description = request.getParameter("description");
        String skills = request.getParameter("skills");

        // Insert job into the database
        try (Connection conn = DBUtils.getConnection()) {
            String sql = "INSERT INTO jobs (recruiter_id, job_title, company_name, location, salary, description, skills) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, recruiterId);
                stmt.setString(2, jobTitle);
                stmt.setString(3, companyName);
                stmt.setString(4, location);
                stmt.setString(5, salary);
                stmt.setString(6, description);
                stmt.setString(7, skills);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Job successfully added by recruiter ID: " + recruiterId);
                    
                    // Show an alert and navigate to recruiter_user.html after clicking OK
                    response.setContentType("text/html");
                    response.getWriter().println("<script>"
                        + "alert('Job added successfully!');"
                        + "window.location.href='recruiter_user.html';"
                        + "</script>");
                } else {
                    response.getWriter().println("<h1>Error: Job could not be added.</h1>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("<h1>Database error: " + e.getMessage() + "</h1>");
        }
    }
}
