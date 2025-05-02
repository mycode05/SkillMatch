import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/StudentWelcomeServlet")
public class StudentWelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("⚠️ Session expired! Redirecting to login.");
            response.sendRedirect("student_login.html");
            return;
        }

        // ✅ Keep session active
        session.setMaxInactiveInterval(60 * 60);
        
        String username = (String) session.getAttribute("username");
        System.out.println("✅ Student " + username + " is accessing welcome page.");

        response.sendRedirect("student_welcome.html");
    }
}