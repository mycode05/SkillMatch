import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/SearchJobServlet")
public class SearchJobServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String searchQuery = request.getParameter("query");
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            out.println("<p style='color: red;'>Please enter a valid search term.</p>");
            return;
        }

        searchQuery = searchQuery.trim().toLowerCase();
        System.out.println("🔍 Search query received: " + searchQuery);

        try (Connection conn = DBUtils.getConnection()) {
            if (conn == null) {
                out.println("<p style='color: red;'>Database connection error.</p>");
                return;
            }

            String sql = "SELECT job_id, recruiter_id, job_title, company_name, location, salary, description, skills " +
                         "FROM jobs WHERE LOWER(job_title) LIKE ? OR LOWER(location) LIKE ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String wildcardQuery = "%" + searchQuery + "%";
                stmt.setString(1, wildcardQuery);
                stmt.setString(2, wildcardQuery);

                try (ResultSet rs = stmt.executeQuery()) {
                    boolean hasResults = false;
                    StringBuilder htmlResponse = new StringBuilder();

                    while (rs.next()) {
                        hasResults = true;
                        int jobId = rs.getInt("job_id");
                        int recruiterId = rs.getInt("recruiter_id");
                        String jobTitle = rs.getString("job_title");
                        String company = rs.getString("company_name");
                        String location = rs.getString("location");
                        String salary = rs.getString("salary");
                        String description = rs.getString("description");
                        String skills = rs.getString("skills");

                        // Generate job listing HTML
                        htmlResponse.append("<div class='job-card'>")
                                    .append("<h3>").append(jobTitle).append("</h3>")
                                    .append("<p><strong>Company:</strong> ").append(company).append("</p>")
                                    .append("<p><strong>Location:</strong> ").append(location).append("</p>")
                                    .append("<p><strong>Salary:</strong> ").append(salary).append("</p>")
                                    .append("<p><strong>Description:</strong> ").append(description).append("</p>")
                                    .append("<p><strong>Skills:</strong> ").append(skills).append("</p>")
                                    .append("<button class='apply-btn' onclick='openApplyForm(")
                                    .append(jobId).append(", ").append(recruiterId).append(")'>Apply</button>")
                                    .append("</div>");
                    }

                    if (!hasResults) {
                        htmlResponse.append("<p style='color: red;'>No jobs found for: <strong>")
                                    .append(searchQuery)
                                    .append("</strong></p>");
                    }

                    out.println(htmlResponse.toString());
                }
            }
        } catch (Exception e) {
            out.println("<p style='color: red;'>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }
}
