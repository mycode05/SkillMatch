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

@WebServlet("/ViewApplicationsServlet")
public class ViewApplicationsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("recruiterId") == null) {
            out.println("<h3 style='color: red; text-align: center;'>You must be logged in to view applications.</h3>");
            return;
        }

        int recruiterId = (Integer) session.getAttribute("recruiterId");

        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT ja.application_id, ja.job_id, s.username, s.email, ja.resume_path, ja.description, ja.applied_at " +
               "FROM job_applications ja " +
               "JOIN users s ON ja.user_id = s.id " +
               "WHERE ja.recruiter_id = ?";

            
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();

            out.println("<style>");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #800080; padding: 10px; text-align: left; }");
            out.println("th { background-color: #4B0082; color: white; }");
            out.println("tr:nth-child(even) { background-color: #E6E6FA; }");
            out.println("tr:hover { background-color: #D8BFD8; }");
            out.println("a { text-decoration: none; color: #4B0082; font-weight: bold; }");
            out.println("</style>");

            out.println("<table>");
            out.println("<tr><th>Application ID</th><th>Job ID</th><th>Student Name</th><th>Email</th><th>Resume</th><th>Description</th><th>Applied At</th></tr>");
            
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("application_id") + "</td>");
                out.println("<td>" + rs.getInt("job_id") + "</td>");
                out.println("<td>" + rs.getString("username") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td><a href='" + rs.getString("resume_path") + "' download>Download</a></td>");
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
