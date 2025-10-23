# Ball Sorting System for FTC Competition

## 🎯 Overview

This ball sorting system uses a REV Color Sensor V3 to detect ball colors and automatically routes them:
- **Correct Color Balls** → Sent to shooter motor (shooting position)
- **Wrong Color Balls** → Ejected through reject motor

## 🔧 Hardware Setup

### Motors Required (TalonFX/Kraken)
1. **Intake Motor (CAN ID 3)** - Brings balls into the system
2. **Shooter Motor (CAN ID 4)** - Sends correct color balls to shooting position
3. **Reject Motor (CAN ID 5)** - Ejects wrong color balls
4. **Drive Motors (CAN IDs 1 & 2)** - Left and right drive (already configured)

### Sensor
- **REV Color Sensor V3** - Connected to I2C onboard port

## 🎮 Operation

### Teleop Controls
- **A Button (Button 1)** - Enable intake and start sorting
- **B Button (Button 2)** - Disable intake and stop sorting
- **Left Joystick Y-Axis** - Forward/backward drive
- **Left Joystick X-Axis** - Turning

### How It Works

1. **Press A Button** to enable the intake system
2. **Intake motor runs** continuously, pulling balls in
3. **When a ball is detected** (proximity sensor triggers):
   - System stops intake motor
   - Color sensor reads the ball color
   - System determines routing:
     - ✅ **Correct color** → Shooter motor runs, ball sent to shooting position
     - ❌ **Wrong color** → Reject motor runs, ball ejected
4. **After routing**, system clears and returns to IDLE state
5. **Ready for next ball**

## ⚙️ Configuration

### Setting Your Target Color

Open `Constants.java` and find this line (around line 34):

```java
public static final TargetColor DESIRED_COLOR = TargetColor.BLUE;
```

**Change it to your desired color:**
- `TargetColor.RED`
- `TargetColor.BLUE`
- `TargetColor.GREEN`
- `TargetColor.YELLOW`

### Adjusting Motor Speeds

In `Constants.java`, adjust these speeds (0.0 to 1.0):

```java
public static final double INTAKE_SPEED = 0.7;      // Intake motor speed
public static final double SHOOTER_SPEED = 0.8;     // Shooter motor speed
public static final double REJECT_SPEED = 0.75;     // Reject motor speed
```

### Timing Adjustments

```java
public static final long SORT_DURATION_MS = 500;    // How long to run shooter/reject
public static final long BALL_CLEAR_TIME_MS = 300;  // Time to ensure ball cleared
```

### Proximity Threshold

Adjust when a ball is detected:

```java
public static final int PROXIMITY_THRESHOLD = 100;  // Higher = closer detection
```

### Color Confidence

Minimum confidence for color matching (0.0 to 1.0):

```java
public static final double COLOR_CONFIDENCE_THRESHOLD = 0.8;
```

## 📊 SmartDashboard Telemetry

View these values on SmartDashboard:
- **Sorting State** - Current state (IDLE, BALL_DETECTED, ROUTING_TO_SHOOTER, etc.)
- **Ball Detected** - Boolean indicating if ball is present
- **Last Color** - Last detected ball color
- **Proximity** - Current proximity sensor value
- **Red/Green/Blue** - RGB color values
- **Intake Enabled** - Whether intake is active
- **Target Color** - Your configured target color

## 🔄 State Machine Flow

```
IDLE
  └─> Ball enters (proximity triggered)
      └─> BALL_DETECTED
          ├─> Color matches target
          │   └─> ROUTING_TO_SHOOTER
          │       └─> CLEARING
          │           └─> IDLE (ready for next)
          │
          └─> Color doesn't match
              └─> ROUTING_TO_REJECT
                  └─> CLEARING
                      └─> IDLE (ready for next)
```

## 🏗️ Project Structure

```
src/main/java/frc/robot/
├── Robot.java                    # Main robot class with drive and ball sorting
├── BallSortingSubsystem.java     # Ball sorting logic and state machine
├── Constants.java                # All configuration constants
└── Main.java                     # Entry point
```

## 🔍 Troubleshooting

### Ball not detected
- Check proximity threshold value
- Verify color sensor wiring
- Check SmartDashboard "Proximity" value

### Wrong color routing
- Calibrate color values in `Constants.Colors`
- Adjust `COLOR_CONFIDENCE_THRESHOLD`
- Check lighting conditions

### Motors not running
- Verify CAN IDs match hardware
- Check motor controller connections
- Ensure intake is enabled (A button pressed)

### Ball gets stuck
- Increase `SORT_DURATION_MS`
- Adjust motor speeds
- Check mechanical alignment

## 📝 Testing Checklist

- [ ] Color sensor detects balls (check proximity value)
- [ ] Correct color balls go to shooter
- [ ] Wrong color balls get ejected
- [ ] System returns to IDLE after each ball
- [ ] Multiple balls can be processed in sequence
- [ ] Intake enable/disable buttons work
- [ ] All motors run in correct direction
- [ ] Timing is appropriate for your mechanism

## 🎓 Competition Tips

1. **Test with actual game pieces** - Color values may differ
2. **Adjust for lighting** - Field lighting affects color detection
3. **Practice driver controls** - Train drivers on A/B button usage
4. **Monitor SmartDashboard** - Watch for issues during matches
5. **Have color presets ready** - Different strategies may need different target colors

## 🚀 Next Steps

1. **Deploy to robot**: `./gradlew deploy`
2. **Test each motor individually** to verify directions
3. **Calibrate color values** under competition lighting
4. **Fine-tune timing values** for your specific mechanism
5. **Practice with drivers** to ensure smooth operation

## 💡 Advanced Features (Future Enhancements)

- Count balls of each color
- Track balls in shooter queue
- Automatic shooter activation
- Multi-color target selection
- Ball jam detection and recovery

---

**Good luck at your FTC competition! 🏆**
