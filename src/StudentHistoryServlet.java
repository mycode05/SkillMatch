import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StudentHistoryServlet")
public class StudentHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false); // Get session without creating a new one
        if (session == null || session.getAttribute("userId") == null) {
            out.println("<h3 style='color: #800080; text-align: center;'>You must be logged in to view your application history.</h3>");
            return;
        }

        int userId = (Integer) session.getAttribute("userId"); // Get logged-in user ID

        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT ja.application_id, ja.job_id, r.company_name, ja.resume_path, ja.description, ja.applied_at " +
                           "FROM job_applications ja " +
                           "JOIN recruiters r ON ja.recruiter_id = r.id " +
                           "WHERE ja.user_id = ?";
            
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            // Table styling with purple theme
            out.println("<style>");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #800080; padding: 10px; text-align: left; }");
            out.println("th { background-color: #4B0082; color: white; }");
            out.println("tr:nth-child(even) { background-color: #E6E6FA; }");
            out.println("tr:hover { background-color: #D8BFD8; }");
            out.println("a { text-decoration: none; color: #4B0082; font-weight: bold; }");
            out.println("</style>");

            // Table header
            out.println("<table>");
            out.println("<tr><th>Application ID</th><th>Job ID</th><th>Company</th><th>Resume</th><th>Description</th><th>Applied At</th></tr>");
            
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("application_id") + "</td>");
                out.println("<td>" + rs.getInt("job_id") + "</td>");
                out.println("<td>" + rs.getString("company_name") + "</td>");
                out.println("<td><a href='" + rs.getString("resume_path") + "' target='_blank'>View</a></td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + rs.getTimestamp("applied_at") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            if (!hasRecords) {
                out.println("<h3 style='color: #800080; text-align: center;'>No applications found.</h3>");
            }
        } catch (Exception e) {
            out.println("<h3 style='color: red; text-align: center;'>Error retrieving data.</h3>");
            e.printStackTrace(out);
        }
    }
}