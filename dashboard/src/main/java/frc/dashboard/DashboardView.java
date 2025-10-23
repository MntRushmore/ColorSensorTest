package frc.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Main Dashboard View - UI Layout and Components
 * 
 * This class creates the visual layout of the dashboard with:
 * - Header with team branding and connection status
 * - Color sensor visualization panel
 * - Robot state and subsystem status
 * - Manual controls for testing and calibration
 * - Settings and configuration panel
 * 
 * The layout uses JavaFX BorderPane with:
 * - TOP: Header with title and status
 * - CENTER: Grid of telemetry panels
 * - RIGHT: Control panels
 * - BOTTOM: Status bar
 * 
 * HOW TO EXTEND:
 * - Add new panels: Create new methods like createMyPanel() and add to grid
 * - Modify layout: Adjust GridPane rows/columns in setupLayout()
 * - Add controls: Create new buttons/sliders in the control panel
 * - Customize styling: Modify CSS classes or inline styles
 * 
 * @author FRC Dashboard Team
 */
public class DashboardView {
    
    // Root container for the entire dashboard
    private BorderPane root;
    
    // ===== Telemetry Display Components =====
    
    // Color Sensor Panel
    private Label proximityLabel;
    private Label redValueLabel;
    private Label greenValueLabel;
    private Label blueValueLabel;
    private Label confidenceLabel;
    private Label detectedColorLabel;
    private Rectangle colorPreview;
    
    // Robot State Panel
    private Label sortingStateLabel;
    private Label ballDetectedLabel;
    private Label lastColorLabel;
    private Label targetColorLabel;
    private Label intakeEnabledLabel;
    
    // ===== Control Components =====
    
    // Connection controls
    private TextField robotIPField;
    private Button connectButton;
    private Label connectionStatusLabel;
    
    // Testing controls
    private Slider motorSpeedSlider;
    private Label speedValueLabel;
    private Button testIntakeButton;
    private Button testShooterButton;
    private Button testRejectButton;
    private Button stopAllButton;
    
    // Calibration controls
    private Button calibrateRedButton;
    private Button calibrateBlueButton;
    private Button calibrateGreenButton;
    private Button calibrateYellowButton;
    private Button saveCalibrationButton;
    
    // Target color selection
    private ComboBox<String> targetColorCombo;
    private Button setTargetButton;
    
    // Threshold adjustments
    private Slider proximityThresholdSlider;
    private Slider confidenceThresholdSlider;
    private Label proximityThresholdLabel;
    private Label confidenceThresholdLabel;
    
    /**
     * Constructor - initializes the dashboard layout
     */
    public DashboardView() {
        root = new BorderPane();
        root.getStyleClass().add("dashboard-root");
        setupLayout();
    }
    
    /**
     * Get the root node for adding to Scene
     * @return Root BorderPane containing all dashboard components
     */
    public BorderPane getRoot() {
        return root;
    }
    
    /**
     * Setup the complete dashboard layout
     * Organizes all panels into a clean, professional interface
     */
    private void setupLayout() {
        // Create header with FTC branding
        root.setTop(createHeader());
        
        // Create center panel with telemetry displays
        root.setCenter(createCenterPanel());
        
        // Create right sidebar with controls
        root.setRight(createControlPanel());
        
        // Create bottom status bar
        root.setBottom(createStatusBar());
    }
    
    /**
     * Create header with title and connection status
     * Purple-themed with FTC branding
     */
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.getStyleClass().add("header");
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        
        // Title
        Label titleLabel = new Label("🤖 FRC ROBOT DASHBOARD");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        // Subtitle
        Label subtitleLabel = new Label("Ball Sorting System • Real-Time Telemetry");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        subtitleLabel.setStyle("-fx-text-fill: #E0B0FF;");
        
        // Connection status indicator
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER);
        
        connectionStatusLabel = new Label("● DISCONNECTED");
        connectionStatusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        connectionStatusLabel.setStyle("-fx-text-fill: #FF6B6B;");
        
        statusBox.getChildren().add(connectionStatusLabel);
        
        header.getChildren().addAll(titleLabel, subtitleLabel, statusBox);
        return header;
    }
    
    /**
     * Create center panel with telemetry displays
     * Uses GridPane for organized layout of sensor data
     */
    private GridPane createCenterPanel() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(20);
        grid.getStyleClass().add("telemetry-grid");
        
        // Configure column constraints for responsive layout
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);
        
        // Add panels to grid
        grid.add(createColorSensorPanel(), 0, 0);
        grid.add(createRobotStatePanel(), 1, 0);
        grid.add(createColorVisualizationPanel(), 0, 1);
        grid.add(createSystemStatusPanel(), 1, 1);
        
        return grid;
    }
    
    /**
     * Create color sensor data panel
     * Displays proximity, RGB values, and confidence
     */
    private VBox createColorSensorPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("telemetry-panel");
        panel.setPadding(new Insets(15));
        
        // Panel title
        Label title = new Label("🎨 COLOR SENSOR DATA");
        title.getStyleClass().add("panel-title");
        
        // Create data display grid
        GridPane dataGrid = new GridPane();
        dataGrid.setHgap(10);
        dataGrid.setVgap(8);
        
        // Proximity
        dataGrid.add(createDataLabel("Proximity:"), 0, 0);
        proximityLabel = createValueLabel("0");
        dataGrid.add(proximityLabel, 1, 0);
        
        // Red value
        dataGrid.add(createDataLabel("Red:"), 0, 1);
        redValueLabel = createValueLabel("0.000");
        dataGrid.add(redValueLabel, 1, 1);
        
        // Green value
        dataGrid.add(createDataLabel("Green:"), 0, 2);
        greenValueLabel = createValueLabel("0.000");
        dataGrid.add(greenValueLabel, 1, 2);
        
        // Blue value
        dataGrid.add(createDataLabel("Blue:"), 0, 3);
        blueValueLabel = createValueLabel("0.000");
        dataGrid.add(blueValueLabel, 1, 3);
        
        // Confidence
        dataGrid.add(createDataLabel("Confidence:"), 0, 4);
        confidenceLabel = createValueLabel("0.00");
        dataGrid.add(confidenceLabel, 1, 4);
        
        // Detected color
        dataGrid.add(createDataLabel("Detected:"), 0, 5);
        detectedColorLabel = createValueLabel("None");
        detectedColorLabel.getStyleClass().add("highlighted-value");
        dataGrid.add(detectedColorLabel, 1, 5);
        
        panel.getChildren().addAll(title, dataGrid);
        return panel;
    }
    
    /**
     * Create color visualization panel
     * Shows live RGB color preview
     */
    private VBox createColorVisualizationPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("telemetry-panel");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("🌈 COLOR PREVIEW");
        title.getStyleClass().add("panel-title");
        
        // Color preview rectangle
        colorPreview = new Rectangle(200, 150);
        colorPreview.setFill(Color.rgb(0, 0, 0));
        colorPreview.setArcWidth(10);
        colorPreview.setArcHeight(10);
        colorPreview.setStyle("-fx-stroke: white; -fx-stroke-width: 2;");
        
        VBox previewBox = new VBox(colorPreview);
        previewBox.setAlignment(Pos.CENTER);
        
        panel.getChildren().addAll(title, previewBox);
        return panel;
    }
    
    /**
     * Create robot state panel
     * Shows current sorting state and ball detection
     */
    private VBox createRobotStatePanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("telemetry-panel");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("🤖 ROBOT STATE");
        title.getStyleClass().add("panel-title");
        
        GridPane dataGrid = new GridPane();
        dataGrid.setHgap(10);
        dataGrid.setVgap(8);
        
        // Sorting state
        dataGrid.add(createDataLabel("State:"), 0, 0);
        sortingStateLabel = createValueLabel("IDLE");
        sortingStateLabel.getStyleClass().add("state-label");
        dataGrid.add(sortingStateLabel, 1, 0);
        
        // Ball detected
        dataGrid.add(createDataLabel("Ball Detected:"), 0, 1);
        ballDetectedLabel = createValueLabel("false");
        dataGrid.add(ballDetectedLabel, 1, 1);
        
        // Last color
        dataGrid.add(createDataLabel("Last Color:"), 0, 2);
        lastColorLabel = createValueLabel("None");
        dataGrid.add(lastColorLabel, 1, 2);
        
        // Target color
        dataGrid.add(createDataLabel("Target Color:"), 0, 3);
        targetColorLabel = createValueLabel("BLUE");
        targetColorLabel.getStyleClass().add("highlighted-value");
        dataGrid.add(targetColorLabel, 1, 3);
        
        // Intake enabled
        dataGrid.add(createDataLabel("Intake Enabled:"), 0, 4);
        intakeEnabledLabel = createValueLabel("false");
        dataGrid.add(intakeEnabledLabel, 1, 4);
        
        panel.getChildren().addAll(title, dataGrid);
        return panel;
    }
    
    /**
     * Create system status panel
     * Shows overall system health and statistics
     */
    private VBox createSystemStatusPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("telemetry-panel");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("📊 SYSTEM STATUS");
        title.getStyleClass().add("panel-title");
        
        Label statusText = new Label("System operational\nAll subsystems ready\nNetworkTables active");
        statusText.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        panel.getChildren().addAll(title, statusText);
        return panel;
    }
    
    /**
     * Create right sidebar control panel
     * Contains connection, testing, and calibration controls
     */
    private ScrollPane createControlPanel() {
        VBox controlsContainer = new VBox(15);
        controlsContainer.setPadding(new Insets(20));
        controlsContainer.setMinWidth(350);
        controlsContainer.setMaxWidth(350);
        controlsContainer.getStyleClass().add("controls-panel");
        
        // Add all control sections
        controlsContainer.getChildren().addAll(
            createConnectionPanel(),
            createTestingPanel(),
            createCalibrationPanel(),
            createConfigPanel()
        );
        
        ScrollPane scrollPane = new ScrollPane(controlsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #2D1B3D;");
        
        return scrollPane;
    }
    
    /**
     * Create connection control panel
     * Allows user to connect to robot by IP/team number
     */
    private VBox createConnectionPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("control-section");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("🔗 CONNECTION");
        title.getStyleClass().add("control-title");
        
        robotIPField = new TextField("localhost");
        robotIPField.setPromptText("Robot IP or 'localhost'");
        robotIPField.getStyleClass().add("control-field");
        
        connectButton = new Button("CONNECT TO ROBOT");
        connectButton.getStyleClass().add("control-button");
        connectButton.setMaxWidth(Double.MAX_VALUE);
        
        panel.getChildren().addAll(title, robotIPField, connectButton);
        return panel;
    }
    
    /**
     * Create testing control panel
     * Manual motor control for testing subsystems
     */
    private VBox createTestingPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("control-section");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("🧪 MANUAL TESTING");
        title.getStyleClass().add("control-title");
        
        // Speed slider
        Label speedLabel = new Label("Motor Speed:");
        speedLabel.setStyle("-fx-text-fill: white;");
        
        motorSpeedSlider = new Slider(0, 1.0, 0.5);
        motorSpeedSlider.setShowTickLabels(true);
        motorSpeedSlider.setShowTickMarks(true);
        motorSpeedSlider.setMajorTickUnit(0.25);
        motorSpeedSlider.getStyleClass().add("control-slider");
        
        speedValueLabel = createValueLabel("0.50");
        
        HBox speedBox = new HBox(10, motorSpeedSlider, speedValueLabel);
        speedBox.setAlignment(Pos.CENTER_LEFT);
        
        // Test buttons
        testIntakeButton = new Button("TEST INTAKE");
        testIntakeButton.getStyleClass().add("test-button");
        testIntakeButton.setMaxWidth(Double.MAX_VALUE);
        
        testShooterButton = new Button("TEST SHOOTER");
        testShooterButton.getStyleClass().add("test-button");
        testShooterButton.setMaxWidth(Double.MAX_VALUE);
        
        testRejectButton = new Button("TEST REJECT");
        testRejectButton.getStyleClass().add("test-button");
        testRejectButton.setMaxWidth(Double.MAX_VALUE);
        
        stopAllButton = new Button("⏹ STOP ALL");
        stopAllButton.getStyleClass().add("stop-button");
        stopAllButton.setMaxWidth(Double.MAX_VALUE);
        
        panel.getChildren().addAll(
            title, speedLabel, speedBox,
            testIntakeButton, testShooterButton, testRejectButton, stopAllButton
        );
        return panel;
    }
    
    /**
     * Create calibration control panel
     * Color calibration and threshold adjustment
     */
    private VBox createCalibrationPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("control-section");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("⚙️ CALIBRATION");
        title.getStyleClass().add("control-title");
        
        // Color calibration buttons
        calibrateRedButton = new Button("🔴 CALIBRATE RED");
        calibrateRedButton.getStyleClass().add("calibrate-button");
        calibrateRedButton.setMaxWidth(Double.MAX_VALUE);
        
        calibrateBlueButton = new Button("🔵 CALIBRATE BLUE");
        calibrateBlueButton.getStyleClass().add("calibrate-button");
        calibrateBlueButton.setMaxWidth(Double.MAX_VALUE);
        
        calibrateGreenButton = new Button("🟢 CALIBRATE GREEN");
        calibrateGreenButton.getStyleClass().add("calibrate-button");
        calibrateGreenButton.setMaxWidth(Double.MAX_VALUE);
        
        calibrateYellowButton = new Button("🟡 CALIBRATE YELLOW");
        calibrateYellowButton.getStyleClass().add("calibrate-button");
        calibrateYellowButton.setMaxWidth(Double.MAX_VALUE);
        
        saveCalibrationButton = new Button("💾 SAVE CALIBRATION");
        saveCalibrationButton.getStyleClass().add("save-button");
        saveCalibrationButton.setMaxWidth(Double.MAX_VALUE);
        
        panel.getChildren().addAll(
            title,
            calibrateRedButton, calibrateBlueButton,
            calibrateGreenButton, calibrateYellowButton,
            saveCalibrationButton
        );
        return panel;
    }
    
    /**
     * Create configuration panel
     * Target color and threshold settings
     */
    private VBox createConfigPanel() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("control-section");
        panel.setPadding(new Insets(15));
        
        Label title = new Label("⚡ CONFIGURATION");
        title.getStyleClass().add("control-title");
        
        // Target color selector
        Label targetLabel = new Label("Target Color:");
        targetLabel.setStyle("-fx-text-fill: white;");
        
        targetColorCombo = new ComboBox<>();
        targetColorCombo.getItems().addAll("RED", "BLUE", "GREEN", "YELLOW");
        targetColorCombo.setValue("BLUE");
        targetColorCombo.getStyleClass().add("control-combo");
        targetColorCombo.setMaxWidth(Double.MAX_VALUE);
        
        setTargetButton = new Button("SET TARGET");
        setTargetButton.getStyleClass().add("control-button");
        setTargetButton.setMaxWidth(Double.MAX_VALUE);
        
        // Proximity threshold
        Label proximityLabel = new Label("Proximity Threshold:");
        proximityLabel.setStyle("-fx-text-fill: white;");
        
        proximityThresholdSlider = new Slider(50, 200, 100);
        proximityThresholdSlider.setShowTickMarks(true);
        proximityThresholdSlider.getStyleClass().add("control-slider");
        
        proximityThresholdLabel = createValueLabel("100");
        
        HBox proximityBox = new HBox(10, proximityThresholdSlider, proximityThresholdLabel);
        proximityBox.setAlignment(Pos.CENTER_LEFT);
        
        // Confidence threshold
        Label confidenceLabel = new Label("Confidence Threshold:");
        confidenceLabel.setStyle("-fx-text-fill: white;");
        
        confidenceThresholdSlider = new Slider(0.5, 1.0, 0.8);
        confidenceThresholdSlider.setShowTickMarks(true);
        confidenceThresholdSlider.getStyleClass().add("control-slider");
        
        confidenceThresholdLabel = createValueLabel("0.80");
        
        HBox confidenceBox = new HBox(10, confidenceThresholdSlider, confidenceThresholdLabel);
        confidenceBox.setAlignment(Pos.CENTER_LEFT);
        
        panel.getChildren().addAll(
            title,
            targetLabel, targetColorCombo, setTargetButton,
            proximityLabel, proximityBox,
            confidenceLabel, confidenceBox
        );
        return panel;
    }
    
    /**
     * Create status bar at bottom
     * Shows connection info and dashboard status
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("status-bar");
        
        Label statusLabel = new Label("FRC Dashboard v1.0 | Ready");
        statusLabel.setStyle("-fx-text-fill: #E0B0FF; -fx-font-size: 12px;");
        
        statusBar.getChildren().add(statusLabel);
        return statusBar;
    }
    
    // ===== Helper Methods for Creating UI Components =====
    
    private Label createDataLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("data-label");
        return label;
    }
    
    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("value-label");
        return label;
    }
    
    // ===== Getters for Controller Access =====
    
    public Label getProximityLabel() { return proximityLabel; }
    public Label getRedValueLabel() { return redValueLabel; }
    public Label getGreenValueLabel() { return greenValueLabel; }
    public Label getBlueValueLabel() { return blueValueLabel; }
    public Label getConfidenceLabel() { return confidenceLabel; }
    public Label getDetectedColorLabel() { return detectedColorLabel; }
    public Rectangle getColorPreview() { return colorPreview; }
    
    public Label getSortingStateLabel() { return sortingStateLabel; }
    public Label getBallDetectedLabel() { return ballDetectedLabel; }
    public Label getLastColorLabel() { return lastColorLabel; }
    public Label getTargetColorLabel() { return targetColorLabel; }
    public Label getIntakeEnabledLabel() { return intakeEnabledLabel; }
    
    public Label getConnectionStatusLabel() { return connectionStatusLabel; }
    public TextField getRobotIPField() { return robotIPField; }
    public Button getConnectButton() { return connectButton; }
    
    public Slider getMotorSpeedSlider() { return motorSpeedSlider; }
    public Label getSpeedValueLabel() { return speedValueLabel; }
    public Button getTestIntakeButton() { return testIntakeButton; }
    public Button getTestShooterButton() { return testShooterButton; }
    public Button getTestRejectButton() { return testRejectButton; }
    public Button getStopAllButton() { return stopAllButton; }
    
    public Button getCalibrateRedButton() { return calibrateRedButton; }
    public Button getCalibrateBlueButton() { return calibrateBlueButton; }
    public Button getCalibrateGreenButton() { return calibrateGreenButton; }
    public Button getCalibrateYellowButton() { return calibrateYellowButton; }
    public Button getSaveCalibrationButton() { return saveCalibrationButton; }
    
    public ComboBox<String> getTargetColorCombo() { return targetColorCombo; }
    public Button getSetTargetButton() { return setTargetButton; }
    public Slider getProximityThresholdSlider() { return proximityThresholdSlider; }
    public Slider getConfidenceThresholdSlider() { return confidenceThresholdSlider; }
    public Label getProximityThresholdLabel() { return proximityThresholdLabel; }
    public Label getConfidenceThresholdLabel() { return confidenceThresholdLabel; }
}
