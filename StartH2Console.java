import org.h2.tools.Server;
import java.sql.SQLException;

public class StartH2Console {
    public static void main(String[] args) {
        try {
            Server server = Server.createWebServer(
                "-web", 
                "-webAllowOthers", 
                "-webPort", "8082"
            ).start();
            
            System.out.println("==========================================");
            System.out.println("H2 Console Started Successfully!");
            System.out.println("==========================================");
            System.out.println("Open your web browser and go to:");
            System.out.println("  http://localhost:8082");
            System.out.println();
            System.out.println("Connection Settings:");
            System.out.println("  JDBC URL: jdbc:h2:./bookingdb");
            System.out.println("  User Name: sa");
            System.out.println("  Password: (leave empty)");
            System.out.println();
            System.out.println("Press Ctrl+C to stop the server");
            System.out.println("==========================================");
            
            // Keep server running
            Thread.sleep(Long.MAX_VALUE);
        } catch (SQLException e) {
            System.err.println("Error starting H2 Console: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Server stopped.");
        }
    }
}