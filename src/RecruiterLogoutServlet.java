import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RecruiterLogoutServlet")
public class RecruiterLogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Only destroy session when recruiter explicitly logs out
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Destroy session
            System.out.println("Recruiter logged out.");
        }

        response.sendRedirect("recruiter_login.html"); // Redirect to login page
    }
}
