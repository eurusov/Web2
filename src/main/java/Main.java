import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlet.ApiServlet;
import servlet.LoginServlet;
import servlet.RegistrationServlet;

public class Main {

    public static void main(String[] args) throws Exception {
        ApiServlet apiServlet = new ApiServlet();
        RegistrationServlet regServlet = new RegistrationServlet();
        LoginServlet loginServlet = new LoginServlet();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(apiServlet), "/api/*");
        context.addServlet(new ServletHolder(loginServlet), "/");
        context.addServlet(new ServletHolder(regServlet), "/register");
        context.addServlet(new ServletHolder(loginServlet), "/login");

        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        server.join();
    }
}
