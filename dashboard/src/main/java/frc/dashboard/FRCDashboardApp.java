package frc.dashboard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * FRC Robot Dashboard Application - Main Entry Point
 * 
 * This is a custom JavaFX-based dashboard for FRC robots that provides:
 * - Real-time telemetry visualization from NetworkTables
 * - Color sensor data display with RGB values and confidence
 * - Robot state and subsystem status monitoring
 * - Manual control interface for testing and calibration
 * - Purple FTC-themed modern UI
 * 
 * HOW TO RUN:
 * 1. Ensure robot is running and NetworkTables server is active
 * 2. Run: ./gradlew :dashboard:run
 * 3. Or build jar: ./gradlew :dashboard:jar
 * 4. Connect to robot IP in the dashboard settings
 * 
 * HOW TO EXTEND:
 * - Add new telemetry: Edit DashboardController to subscribe to more NT entries
 * - Add controls: Create new UI components in DashboardView
 * - Customize theme: Modify FTCTheme class or dashboard.css
 * - Add subsystems: Create new panel classes following existing patterns
 * 
 * @author FRC Dashboard Team
 * @version 1.0.0
 */
public class FRCDashboardApp extends Application {
    
    private NetworkTablesClient ntClient;
    private DashboardController controller;
    
    /**
     * Application entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Initialize and start the JavaFX application
     * Sets up the main window with purple FTC theming
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize NetworkTables client
            ntClient = new NetworkTablesClient();
            
            // Create the main dashboard view
            DashboardView dashboardView = new DashboardView();
            
            // Create controller to manage data flow between NT and UI
            controller = new DashboardController(ntClient, dashboardView);
            
            // Create scene with FTC purple theme
            Scene scene = new Scene(dashboardView.getRoot(), 1400, 900);
            
            // Load custom CSS for purple FTC styling
            String css = getClass().getResource("/dashboard.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Configure main window
            primaryStage.setTitle("FRC Robot Dashboard - Purple Edition");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            
            // Set application icon (if available)
            try {
                Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.out.println("App icon not found, using default");
            }
            
            // Handle window close event - cleanup NetworkTables connection
            primaryStage.setOnCloseRequest(event -> {
                cleanup();
            });
            
            // Show the dashboard
            primaryStage.show();
            
            System.out.println("FRC Dashboard started successfully!");
            System.out.println("Waiting for robot connection...");
            
        } catch (Exception e) {
            System.err.println("Error starting dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cleanup resources when application closes
     * Properly disconnects from NetworkTables to avoid connection leaks
     */
    private void cleanup() {
        System.out.println("Shutting down dashboard...");
        
        if (controller != null) {
            controller.stop();
        }
        
        if (ntClient != null) {
            ntClient.disconnect();
        }
        
        System.out.println("Dashboard closed successfully");
    }
    
    /**
     * JavaFX stop method - called when application is closing
     */
    @Override
    public void stop() {
        cleanup();
    }
}
