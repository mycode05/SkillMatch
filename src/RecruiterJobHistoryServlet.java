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

@WebServlet("/RecruiterJobHistoryServlet")
public class RecruiterJobHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false); // Get existing session, don't create a new one
        if (session == null) {
            out.println("<h3>Session is null. You are not logged in!</h3>");
            return;
        }

        Object recruiterIdObj = session.getAttribute("recruiterId");
        if (recruiterIdObj == null) {
            out.println("<h3>recruiterId is missing from session!</h3>");
            return;
        }

        Integer recruiterId = (Integer) recruiterIdObj; // Safe casting

        // Debug print
        out.println("<h3>Debug: recruiterId found in session: " + recruiterId + "</h3>");

        try (Connection con = DBUtils.getConnection()) {
            String query = "SELECT job_id, job_title, company_name, location, salary FROM jobs WHERE recruiter_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, recruiterId);
            ResultSet rs = ps.executeQuery();

            // Add CSS for styling
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
            out.println("<tr><th>Job ID</th><th>Job Title</th><th>Company</th><th>Location</th><th>Salary</th></tr>");

            // Print job records
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("job_id") + "</td>");
                out.println("<td>" + rs.getString("job_title") + "</td>");
                out.println("<td>" + rs.getString("company_name") + "</td>");
                out.println("<td>" + rs.getString("location") + "</td>");
                out.println("<td>" + (rs.getString("salary") != null ? rs.getString("salary") : "N/A") + "</td>");
                out.println("</tr>");
            }
            out.println("</table>");

            if (!hasRecords) {
                out.println("<h3 style='color: #800080; text-align: center;'>No job listings found.</h3>");
            }

        } catch (Exception e) {
            out.println("<h3 style='color: red; text-align: center;'>Error retrieving data.</h3>");
            e.printStackTrace(out);
        }
    }
}
