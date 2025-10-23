# FRC Robot Dashboard - Purple Edition 🤖💜

A custom JavaFX-based dashboard application for FRC robots featuring:
- Real-time telemetry visualization via NetworkTables
- Color sensor data display with RGB values and confidence metrics
- Robot state and subsystem status monitoring
- Manual control interface for testing and calibration
- Purple FTC-themed modern user interface

![Dashboard Preview](https://img.shields.io/badge/FRC-Dashboard-purple?style=for-the-badge)

## 📋 Features

### 🎨 Color Sensor Visualization
- **Real-time RGB Values**: Display red, green, and blue components (0.0-1.0 range)
- **Live Color Preview**: Visual representation of detected color
- **Proximity Sensor**: Distance measurement to ball
- **Confidence Meter**: Color matching accuracy
- **Detected Color**: Named color identification (Red/Blue/Green/Yellow)

### 🤖 Robot State Monitoring
- **Sorting State**: Current state machine status (IDLE, BALL_DETECTED, ROUTING, etc.)
- **Ball Detection**: Real-time ball presence indicator
- **Last Color**: Most recently processed ball color
- **Target Color**: Currently configured target color
- **Intake Status**: Intake subsystem enable/disable state

### 🧪 Manual Testing Controls
- **Motor Speed Slider**: Adjustable speed control (0.0 - 1.0)
- **Test Intake**: Run intake motor at specified speed
- **Test Shooter**: Run shooter motor at specified speed
- **Test Reject**: Run reject motor at specified speed
- **Emergency Stop**: Stop all motors immediately

### ⚙️ Calibration Tools
- **Color Calibration**: Calibrate Red, Blue, Green, and Yellow targets
- **Save Calibration**: Persist calibration data to robot
- **Proximity Threshold**: Adjust ball detection sensitivity (50-200)
- **Confidence Threshold**: Set minimum color matching confidence (0.5-1.0)
- **Target Color Selection**: Choose desired ball color for sorting

### 🔗 Connection Management
- **Robot IP Configuration**: Connect to robot by IP address or hostname
- **Team Number Support**: Automatic team-based connection
- **Connection Status**: Live connection indicator
- **Auto-reconnect**: Handles network interruptions gracefully

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Gradle 7.0+
- FRC robot running WPILib 2025.3.2+
- Robot code publishing to NetworkTables SmartDashboard table

### Installation

1. **Clone the repository** (if not already in robot project):
```bash
cd /path/to/robot/project
cd dashboard
```

2. **Build the dashboard**:
```bash
./gradlew :dashboard:build
```

3. **Run the dashboard**:
```bash
./gradlew :dashboard:run
```

Or build a standalone JAR:
```bash
./gradlew :dashboard:jar
java -jar dashboard/build/libs/dashboard-1.0.0.jar
```

## 🎮 Usage Guide

### Connecting to Robot

1. **Start your robot** with NetworkTables server running
2. **Launch the dashboard** using `./gradlew :dashboard:run`
3. **Enter robot IP** in the connection field:
   - `localhost` for simulation
   - `10.TE.AM.2` for robot (replace TE.AM with your team number)
   - `roborio-TEAM-frc.local` for mDNS connection
4. **Click "CONNECT TO ROBOT"**
5. Watch the connection status indicator turn green

### Reading Telemetry

The dashboard automatically subscribes to these NetworkTables entries:

| Entry Key | Type | Description |
|-----------|------|-------------|
| `Proximity` | Number | Distance sensor value (0-255) |
| `Red` | Number | Red color component (0.0-1.0) |
| `Green` | Number | Green color component (0.0-1.0) |
| `Blue` | Number | Blue color component (0.0-1.0) |
| `Detected Color` | String | Color name (Red/Blue/Green/Yellow) |
| `Confidence` | Number | Match confidence (0.0-1.0) |
| `Sorting State` | String | State machine state |
| `Ball Detected` | Boolean | Ball presence |
| `Last Color` | String | Last processed color |
| `Target Color` | String | Configured target color |
| `Intake Enabled` | Boolean | Intake subsystem status |

### Manual Testing

1. **Adjust motor speed** with the slider (0.0 - 1.0)
2. **Click test button** for desired motor:
   - `TEST INTAKE` - Run intake motor
   - `TEST SHOOTER` - Run shooter motor
   - `TEST REJECT` - Run reject motor
3. **Click "STOP ALL"** to emergency stop all motors

The dashboard sends these commands to NetworkTables:

```java
// Motor testing
SmartDashboard.putNumber("Test/Intake Speed", speed);
SmartDashboard.putBoolean("Test/Run Intake", true);
SmartDashboard.putNumber("Test/Shooter Speed", speed);
SmartDashboard.putBoolean("Test/Run Shooter", true);
SmartDashboard.putNumber("Test/Reject Speed", speed);
SmartDashboard.putBoolean("Test/Run Reject", true);
```

### Color Calibration

1. **Place ball** of known color near sensor
2. **Wait for stable reading** (RGB values steady)
3. **Click calibration button** for that color
4. **Repeat** for all colors you need
5. **Click "SAVE CALIBRATION"** to persist

Calibration commands sent to NetworkTables:

```java
SmartDashboard.putString("Calibration/Command", "CALIBRATE_RED");
SmartDashboard.putString("Calibration/Command", "CALIBRATE_BLUE");
SmartDashboard.putString("Calibration/Command", "CALIBRATE_GREEN");
SmartDashboard.putString("Calibration/Command", "CALIBRATE_YELLOW");
SmartDashboard.putString("Calibration/Command", "SAVE_CALIBRATION");
```

### Configuration

**Set Target Color:**
1. Select color from dropdown (RED/BLUE/GREEN/YELLOW)
2. Click "SET TARGET"
3. Robot will sort balls matching this color to shooter

**Adjust Thresholds:**
- **Proximity Threshold**: Move slider to adjust ball detection sensitivity
  - Lower = Detect farther away
  - Higher = Require closer proximity
- **Confidence Threshold**: Move slider to adjust color matching strictness
  - Lower = More lenient matching
  - Higher = Require closer color match

Configuration is sent to NetworkTables:
```java
SmartDashboard.putString("Config/Target Color", "BLUE");
SmartDashboard.putNumber("Config/Proximity Threshold", 100);
SmartDashboard.putNumber("Config/Confidence Threshold", 0.8);
```

## 🔧 Integration with Robot Code

### Required Robot Code Changes

To use this dashboard, your robot code must publish data to NetworkTables. Here's how:

#### 1. Publishing Telemetry (Already implemented in BallSortingSubsystem.java)

```java
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// In your periodic method:
SmartDashboard.putNumber("Proximity", colorSensor.getProximity());
SmartDashboard.putNumber("Red", detectedColor.red);
SmartDashboard.putNumber("Green", detectedColor.green);
SmartDashboard.putNumber("Blue", detectedColor.blue);
SmartDashboard.putString("Detected Color", colorName);
SmartDashboard.putNumber("Confidence", match.confidence);
SmartDashboard.putString("Sorting State", currentState.toString());
SmartDashboard.putBoolean("Ball Detected", ballDetected);
SmartDashboard.putString("Last Color", lastDetectedColor);
SmartDashboard.putString("Target Color", Constants.BallSorting.DESIRED_COLOR.toString());
SmartDashboard.putBoolean("Intake Enabled", intakeEnabled);
```

#### 2. Reading Dashboard Commands (Optional - for manual testing)

```java
// In your robot periodic method, read test commands:
boolean runIntake = SmartDashboard.getBoolean("Test/Run Intake", false);
double intakeSpeed = SmartDashboard.getNumber("Test/Intake Speed", 0.0);

if (runIntake) {
    intakeMotor.setControl(new DutyCycleOut(intakeSpeed));
} else {
    intakeMotor.setControl(new DutyCycleOut(0.0));
}

// Similar for shooter and reject motors
```

#### 3. Handling Calibration Commands (Optional)

```java
String calibCommand = SmartDashboard.getString("Calibration/Command", "");

switch (calibCommand) {
    case "CALIBRATE_RED":
        // Save current RGB as red target
        redTarget = colorSensor.getColor();
        break;
    case "CALIBRATE_BLUE":
        blueTarget = colorSensor.getColor();
        break;
    // ... handle other colors and save
}
```

#### 4. Reading Configuration (Optional)

```java
// Read configuration from dashboard
String targetColor = SmartDashboard.getString("Config/Target Color", "BLUE");
int proximityThreshold = (int) SmartDashboard.getNumber("Config/Proximity Threshold", 100);
double confidenceThreshold = SmartDashboard.getNumber("Config/Confidence Threshold", 0.8);
```

## 🎨 Customization

### Modifying the Theme

Edit `src/main/resources/dashboard.css` to customize colors and styling:

```css
/* Change primary purple color */
.header {
    -fx-background-color: linear-gradient(to bottom, #YOUR_COLOR_1, #YOUR_COLOR_2);
}

/* Change panel backgrounds */
.telemetry-panel {
    -fx-background-color: #YOUR_COLOR;
    -fx-border-color: #YOUR_BORDER_COLOR;
}
```

### Adding New Telemetry Fields

1. **Add NetworkTables subscription** in `DashboardController.java`:
```java
ntClient.subscribeNumber("Your/Key", (value) -> {
    Platform.runLater(() -> {
        yourLabel.setText(String.format("%.2f", value));
    });
});
```

2. **Add UI component** in `DashboardView.java`:
```java
private Label yourLabel;

// In panel creation:
yourLabel = createValueLabel("0.0");
dataGrid.add(createDataLabel("Your Label:"), 0, row);
dataGrid.add(yourLabel, 1, row);
```

3. **Add getter** in `DashboardView.java`:
```java
public Label getYourLabel() { return yourLabel; }
```

### Adding New Controls

1. **Add button** in `DashboardView.java`:
```java
private Button yourButton;

yourButton = new Button("YOUR ACTION");
yourButton.getStyleClass().add("control-button");
yourButton.setMaxWidth(Double.MAX_VALUE);
```

2. **Add handler** in `DashboardController.java`:
```java
view.getYourButton().setOnAction(e -> {
    ntClient.setString("Your/Command", "ACTION");
    System.out.println("Action triggered");
});
```

### Creating New Panels

Follow the pattern in `DashboardView.java`:

```java
private VBox createYourCustomPanel() {
    VBox panel = new VBox(10);
    panel.getStyleClass().add("telemetry-panel");
    panel.setPadding(new Insets(15));
    
    Label title = new Label("YOUR PANEL TITLE");
    title.getStyleClass().add("panel-title");
    
    // Add your components
    
    panel.getChildren().addAll(title, /* your components */);
    return panel;
}
```

Then add it to the grid in `setupLayout()`:
```java
grid.add(createYourCustomPanel(), column, row);
```

## 📁 Project Structure

```
dashboard/
├── build.gradle                    # Gradle build configuration
├── settings.gradle                 # Gradle settings
├── README.md                       # This file
└── src/
    └── main/
        ├── java/
        │   └── frc/
        │       └── dashboard/
        │           ├── FRCDashboardApp.java       # Main application entry point
        │           ├── NetworkTablesClient.java   # NT communication layer
        │           ├── DashboardView.java         # UI layout and components
        │           └── DashboardController.java   # UI-NT bridge logic
        └── resources/
            ├── dashboard.css                      # Purple FTC theme stylesheet
            └── icon.png                           # Application icon (optional)
```

## 🔍 Troubleshooting

### Dashboard won't connect to robot

**Problem**: Connection status stays red "DISCONNECTED"

**Solutions**:
1. Verify robot is powered on and NetworkTables server is running
2. Check IP address is correct (try `10.TE.AM.2` format)
3. Ensure firewall isn't blocking port 5810 (NT4) or 1735 (NT3)
4. Try `localhost` if running simulation
5. Check console for connection error messages

### No data showing in telemetry panels

**Problem**: Dashboard connected but values show "0" or "None"

**Solutions**:
1. Verify robot code is publishing to SmartDashboard
2. Check NetworkTables key names match exactly (case-sensitive)
3. Use NetworkTables client app to verify data is being published
4. Check console for subscription errors

### Dashboard freezes or crashes

**Problem**: Application becomes unresponsive

**Solutions**:
1. Check Java version is 17 or higher
2. Increase JVM memory: `java -Xmx512m -jar dashboard.jar`
3. Check for Java exceptions in console output
4. Try running with `--add-opens` JVM flags (included in run task)

### Colors not displaying correctly

**Problem**: Color preview doesn't match actual ball color

**Solutions**:
1. Calibrate colors under actual lighting conditions
2. Adjust confidence threshold (lower if too strict)
3. Verify RGB values are in 0.0-1.0 range (not 0-255)
4. Check color sensor is clean and properly positioned

## 🚀 Advanced Usage

### Running in Simulation Mode

```bash
# Terminal 1 - Start robot simulation
./gradlew simulateJava

# Terminal 2 - Start dashboard
./gradlew :dashboard:run
```

Dashboard will connect to `localhost` and show simulated data.

### Building Standalone JAR

```bash
./gradlew :dashboard:jar

# Run the JAR
java -jar dashboard/build/libs/dashboard-1.0.0.jar
```

### Custom Launch Scripts

Create `run-dashboard.sh`:
```bash
#!/bin/bash
cd "$(dirname "$0")"
java -jar dashboard/build/libs/dashboard-1.0.0.jar
```

Make executable: `chmod +x run-dashboard.sh`

### NetworkTables Debugging

Enable NT debug logging:
```java
// In NetworkTablesClient constructor:
inst.setLogLevel(NetworkTableInstance.kLogLevelDebug);
```

## 📚 Additional Resources

- [WPILib NetworkTables Documentation](https://docs.wpilib.org/en/stable/docs/software/networktables/index.html)
- [JavaFX Documentation](https://openjfx.io/)
- [FRC Programming Documentation](https://docs.wpilib.org/)
- [REV Color Sensor V3 Documentation](https://docs.revrobotics.com/color-sensor)

## 🤝 Contributing

To extend or modify the dashboard:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly with robot
5. Submit a pull request

## 📝 License

This dashboard is part of an FRC robot project and follows the WPILib license.

## 🏆 Credits

**Developed for FRC Team XXXX**
- Purple FTC themed design
- JavaFX-based interface
- NetworkTables integration
- Real-time telemetry visualization

---

**Version**: 1.0.0  
**Last Updated**: 2025-10-23  
**Compatible with**: WPILib 2025.3.2+

🤖 Happy Competing! 💜
