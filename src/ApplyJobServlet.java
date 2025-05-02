import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/ApplyJobServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024)
public class ApplyJobServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get session and validate user login
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("❌ ERROR: No session found! Redirecting to login.");
            response.sendRedirect("student_user.html");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("❌ ERROR: Session found but userId is missing! Redirecting to login.");
            response.sendRedirect("student_user.html");
            return;
        }

        System.out.println("✅ User ID from session: " + userId);

        try {
            // Get form parameters
            int jobId, recruiterId;
            try {
                jobId = Integer.parseInt(request.getParameter("jobId"));
                recruiterId = Integer.parseInt(request.getParameter("recruiterId"));
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid jobId or recruiterId received! Check form submission.");
                e.printStackTrace();
                response.sendRedirect("student_search_job.html");
                return;
            }

            String userDescription = request.getParameter("userDescription");

            System.out.println("🔹 Received Data: ");
            System.out.println("User ID: " + userId);
            System.out.println("Job ID: " + jobId);
            System.out.println("Recruiter ID: " + recruiterId);
            System.out.println("User Description: " + userDescription);

            // File upload handling
            Part resumePart = request.getPart("resume");
            String fileName = userId + "_" + System.currentTimeMillis() + "_" + resumePart.getSubmittedFileName();

            // Ensure the upload directory exists
            String uploadDirPath = getServletContext().getRealPath("") + "uploads";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            // Define the file upload path
            String uploadPath = uploadDirPath + File.separator + fileName;

            // Saving file
            try (InputStream fileContent = resumePart.getInputStream();
                 FileOutputStream fos = new FileOutputStream(uploadPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("✅ Resume uploaded successfully: " + uploadPath);

            // Insert application into the database
            try (Connection conn = DBUtils.getConnection()) {
                if (conn == null) {
                    System.out.println("❌ Database connection failed!");
                    response.sendRedirect("student_search_job.html");
                    return;
                }
                System.out.println("✅ Database connected successfully.");

                // SQL Query
                String sql = "INSERT INTO job_applications (job_id, recruiter_id, user_id, resume_path, description) VALUES (?, ?, ?, ?, ?)";

                System.out.println("🔹 Debugging SQL Execution:");
                System.out.println("SQL Query: " + sql);
                System.out.println("jobId = " + jobId);
                System.out.println("recruiterId = " + recruiterId);
                System.out.println("userId = " + userId);
                System.out.println("resumePath = uploads/" + fileName);
                System.out.println("description = " + userDescription);

                if (jobId == 0 || recruiterId == 0 || userId == 0) {
                    System.out.println("❌ Error: One or more IDs are 0! Fix this before inserting.");
                    response.sendRedirect("student_search_job.html");
                    return;
                }

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, jobId);
                    stmt.setInt(2, recruiterId);
                    stmt.setInt(3, userId);
                    stmt.setString(4, "uploads/" + fileName);
                    stmt.setString(5, userDescription);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("✅ Job application submitted successfully!");
                        response.setContentType("text/html");
                        response.getWriter().println("<script>alert('Application submitted successfully!'); window.location='student_search_job.html';</script>");
                    } else {
                        System.out.println("❌ Failed to insert job application.");
                        response.setContentType("text/html");
                        response.getWriter().println("<script>alert('Failed to submit application! Try again.'); window.location='student_search_job.html';</script>");
                    }
                    
                }
            } catch (SQLException e) {
                System.out.println("❌ SQL Error: " + e.getMessage());
                e.printStackTrace();
                response.sendRedirect("student_search_job.html");
            }
        } catch (Exception e) {
            System.out.println("❌ Error processing job application: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("student_search_job.html");
        }
    }
}
