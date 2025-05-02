import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RecruiterWelcomeServlet")
public class RecruiterWelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("recruiterId") == null) {
            System.out.println("⚠️ Session expired! Redirecting to login.");
            response.sendRedirect("recruiter_login.html");
            return;
        }

        // ✅ Keep session active
        session.setMaxInactiveInterval(60 * 60);
        
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        System.out.println("✅ Recruiter ID " + recruiterId + " is accessing welcome page.");

        response.sendRedirect("recruiter_welcome.html");
    }
}

