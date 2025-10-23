package frc.dashboard;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Dashboard Controller - Bridge Between NetworkTables and UI
 * 
 * This controller manages the data flow between the robot (via NetworkTables)
 * and the JavaFX UI. It handles:
 * - Subscribing to robot telemetry updates
 * - Updating UI elements with live data
 * - Sending commands from UI to robot
 * - Connection management
 * 
 * ARCHITECTURE:
 * Robot → NetworkTables → Controller → JavaFX UI
 * Robot ← NetworkTables ← Controller ← User Input
 * 
 * HOW TO EXTEND:
 * 1. Add new telemetry: Subscribe to NT entries in setupTelemetrySubscriptions()
 * 2. Add new controls: Connect button handlers in setupControlHandlers()
 * 3. Add data processing: Create helper methods for complex data transforms
 * 4. Add logging: Integrate logging framework for debugging
 * 
 * @author FRC Dashboard Team
 */
public class DashboardController {
    
    private NetworkTablesClient ntClient;
    private DashboardView view;
    private Timer updateTimer;
    
    /**
     * Constructor - initializes controller with NT client and view
     * 
     * @param ntClient NetworkTables client for robot communication
     * @param view Dashboard view containing UI components
     */
    public DashboardController(NetworkTablesClient ntClient, DashboardView view) {
        this.ntClient = ntClient;
        this.view = view;
        
        // Setup subscriptions to robot data
        setupTelemetrySubscriptions();
        
        // Setup control button handlers
        setupControlHandlers();
        
        // Start periodic update timer for non-subscribed data
        startPeriodicUpdates();
    }
    
    /**
     * Setup NetworkTables subscriptions for real-time telemetry
     * Each subscription automatically updates UI when robot sends new data
     */
    private void setupTelemetrySubscriptions() {
        
        // ===== Color Sensor Data Subscriptions =====
        
        // Proximity sensor value - distance to ball
        ntClient.subscribeNumber("Proximity", (value) -> {
            Platform.runLater(() -> {
                view.getProximityLabel().setText(String.format("%.0f", value));
            });
        });
        
        // RGB color values (0.0 to 1.0)
        ntClient.subscribeNumber("Red", (value) -> {
            Platform.runLater(() -> {
                view.getRedValueLabel().setText(String.format("%.3f", value));
                updateColorPreview();
            });
        });
        
        ntClient.subscribeNumber("Green", (value) -> {
            Platform.runLater(() -> {
                view.getGreenValueLabel().setText(String.format("%.3f", value));
                updateColorPreview();
            });
        });
        
        ntClient.subscribeNumber("Blue", (value) -> {
            Platform.runLater(() -> {
                view.getBlueValueLabel().setText(String.format("%.3f", value));
                updateColorPreview();
            });
        });
        
        // Detected color name
        ntClient.subscribeString("Detected Color", (value) -> {
            Platform.runLater(() -> {
                view.getDetectedColorLabel().setText(value);
            });
        });
        
        // Color matching confidence (0.0 to 1.0)
        ntClient.subscribeNumber("Confidence", (value) -> {
            Platform.runLater(() -> {
                view.getConfidenceLabel().setText(String.format("%.2f", value));
            });
        });
        
        // ===== Robot State Subscriptions =====
        
        // Current sorting state machine state
        ntClient.subscribeString("Sorting State", (value) -> {
            Platform.runLater(() -> {
                view.getSortingStateLabel().setText(value);
                // Change color based on state
                updateStateColor(value);
            });
        });
        
        // Ball detected boolean
        ntClient.subscribeBoolean("Ball Detected", (value) -> {
            Platform.runLater(() -> {
                view.getBallDetectedLabel().setText(value ? "✓ YES" : "✗ NO");
                view.getBallDetectedLabel().setStyle(
                    "-fx-text-fill: " + (value ? "#4CAF50" : "#FF6B6B") + ";"
                );
            });
        });
        
        // Last detected ball color
        ntClient.subscribeString("Last Color", (value) -> {
            Platform.runLater(() -> {
                view.getLastColorLabel().setText(value);
            });
        });
        
        // Target color configuration
        ntClient.subscribeString("Target Color", (value) -> {
            Platform.runLater(() -> {
                view.getTargetColorLabel().setText(value);
            });
        });
        
        // Intake enabled status
        ntClient.subscribeBoolean("Intake Enabled", (value) -> {
            Platform.runLater(() -> {
                view.getIntakeEnabledLabel().setText(value ? "✓ ENABLED" : "✗ DISABLED");
                view.getIntakeEnabledLabel().setStyle(
                    "-fx-text-fill: " + (value ? "#4CAF50" : "#FF6B6B") + ";"
                );
            });
        });
    }
    
    /**
     * Setup UI control handlers
     * Connect button clicks to NetworkTables commands
     */
    private void setupControlHandlers() {
        
        // ===== Connection Controls =====
        
        view.getConnectButton().setOnAction(e -> {
            String robotIP = view.getRobotIPField().getText();
            reconnectToRobot(robotIP);
        });
        
        // ===== Motor Testing Controls =====
        
        // Update speed label when slider moves
        view.getMotorSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            view.getSpeedValueLabel().setText(String.format("%.2f", newVal.doubleValue()));
        });
        
        // Test intake motor
        view.getTestIntakeButton().setOnAction(e -> {
            double speed = view.getMotorSpeedSlider().getValue();
            ntClient.setNumber("Test/Intake Speed", speed);
            ntClient.setBoolean("Test/Run Intake", true);
            System.out.println("Testing intake at speed: " + speed);
        });
        
        // Test shooter motor
        view.getTestShooterButton().setOnAction(e -> {
            double speed = view.getMotorSpeedSlider().getValue();
            ntClient.setNumber("Test/Shooter Speed", speed);
            ntClient.setBoolean("Test/Run Shooter", true);
            System.out.println("Testing shooter at speed: " + speed);
        });
        
        // Test reject motor
        view.getTestRejectButton().setOnAction(e -> {
            double speed = view.getMotorSpeedSlider().getValue();
            ntClient.setNumber("Test/Reject Speed", speed);
            ntClient.setBoolean("Test/Run Reject", true);
            System.out.println("Testing reject at speed: " + speed);
        });
        
        // Stop all motors
        view.getStopAllButton().setOnAction(e -> {
            ntClient.setBoolean("Test/Run Intake", false);
            ntClient.setBoolean("Test/Run Shooter", false);
            ntClient.setBoolean("Test/Run Reject", false);
            System.out.println("All motors stopped");
        });
        
        // ===== Calibration Controls =====
        
        view.getCalibrateRedButton().setOnAction(e -> {
            ntClient.setString("Calibration/Command", "CALIBRATE_RED");
            System.out.println("Calibrating RED color...");
        });
        
        view.getCalibrateBlueButton().setOnAction(e -> {
            ntClient.setString("Calibration/Command", "CALIBRATE_BLUE");
            System.out.println("Calibrating BLUE color...");
        });
        
        view.getCalibrateGreenButton().setOnAction(e -> {
            ntClient.setString("Calibration/Command", "CALIBRATE_GREEN");
            System.out.println("Calibrating GREEN color...");
        });
        
        view.getCalibrateYellowButton().setOnAction(e -> {
            ntClient.setString("Calibration/Command", "CALIBRATE_YELLOW");
            System.out.println("Calibrating YELLOW color...");
        });
        
        view.getSaveCalibrationButton().setOnAction(e -> {
            ntClient.setString("Calibration/Command", "SAVE_CALIBRATION");
            System.out.println("Saving calibration to file...");
        });
        
        // ===== Configuration Controls =====
        
        // Set target color
        view.getSetTargetButton().setOnAction(e -> {
            String targetColor = view.getTargetColorCombo().getValue();
            ntClient.setString("Config/Target Color", targetColor);
            System.out.println("Target color set to: " + targetColor);
        });
        
        // Update proximity threshold label
        view.getProximityThresholdSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            int threshold = newVal.intValue();
            view.getProximityThresholdLabel().setText(String.valueOf(threshold));
            ntClient.setNumber("Config/Proximity Threshold", threshold);
        });
        
        // Update confidence threshold label
        view.getConfidenceThresholdSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            double threshold = newVal.doubleValue();
            view.getConfidenceThresholdLabel().setText(String.format("%.2f", threshold));
            ntClient.setNumber("Config/Confidence Threshold", threshold);
        });
    }
    
    /**
     * Start periodic updates for connection status and non-subscribed data
     * Runs every 500ms to poll connection state
     */
    private void startPeriodicUpdates() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateConnectionStatus();
            }
        }, 0, 500);
    }
    
    /**
     * Update connection status indicator
     * Shows connected/disconnected state with color coding
     */
    private void updateConnectionStatus() {
        boolean connected = ntClient.isConnected();
        Platform.runLater(() -> {
            if (connected) {
                view.getConnectionStatusLabel().setText("● CONNECTED");
                view.getConnectionStatusLabel().setStyle("-fx-text-fill: #4CAF50;");
            } else {
                view.getConnectionStatusLabel().setText("● DISCONNECTED");
                view.getConnectionStatusLabel().setStyle("-fx-text-fill: #FF6B6B;");
            }
        });
    }
    
    /**
     * Update color preview rectangle with current RGB values
     * Reads RGB labels and updates the visual color box
     */
    private void updateColorPreview() {
        try {
            double red = Double.parseDouble(view.getRedValueLabel().getText());
            double green = Double.parseDouble(view.getGreenValueLabel().getText());
            double blue = Double.parseDouble(view.getBlueValueLabel().getText());
            
            // Convert to 0-255 range and create color
            Color color = Color.color(red, green, blue);
            view.getColorPreview().setFill(color);
            
        } catch (NumberFormatException e) {
            // Ignore parsing errors during initialization
        }
    }
    
    /**
     * Update state label color based on current state
     * Visual feedback for different sorting states
     * 
     * @param state Current sorting state string
     */
    private void updateStateColor(String state) {
        String color;
        switch (state) {
            case "IDLE":
                color = "#E0B0FF"; // Light purple
                break;
            case "BALL_DETECTED":
                color = "#FFD700"; // Gold
                break;
            case "ROUTING_TO_SHOOTER":
                color = "#4CAF50"; // Green
                break;
            case "ROUTING_TO_REJECT":
                color = "#FF6B6B"; // Red
                break;
            case "CLEARING":
                color = "#FFA500"; // Orange
                break;
            default:
                color = "#FFFFFF"; // White
        }
        view.getSortingStateLabel().setStyle("-fx-text-fill: " + color + ";");
    }
    
    /**
     * Reconnect to robot with new IP address
     * Creates new NetworkTables client with specified address
     * 
     * @param robotIP Robot IP address or hostname
     */
    private void reconnectToRobot(String robotIP) {
        System.out.println("Reconnecting to robot at: " + robotIP);
        
        // Disconnect existing client
        ntClient.disconnect();
        
        // Create new client with new address
        ntClient = new NetworkTablesClient(robotIP, NetworkTablesClient.kDefaultPort4);
        
        // Re-setup subscriptions
        setupTelemetrySubscriptions();
        
        System.out.println("Reconnection initiated");
    }
    
    /**
     * Stop the controller and cleanup resources
     * Call this when closing the dashboard
     */
    public void stop() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }
}
