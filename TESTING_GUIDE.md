# Testing Guide for FTC Ball Sorting Robot

## Testing Overview

This guide provides step-by-step instructions for testing all components of your ball sorting robot.

## Test Files Available

1. **MotorTestRobot.java** - Test all motors individually
2. **ColorSensorTestRobot.java** - Test color sensor detection and calibration
3. **Robot.java** - Full ball sorting system

## How to Switch Between Test Modes

### Method 1: Modify Main.java

Edit `src/main/java/frc/robot/Main.java` and change the robot class:

```java
RobotBase.startRobot(MotorTestRobot::new);
```

Options:
- `MotorTestRobot::new` - Motor testing
- `ColorSensorTestRobot::new` - Color sensor testing
- `Robot::new` - Full system (default)

### Method 2: Comment/Uncomment in Main.java

```java
// Motor Testing
// RobotBase.startRobot(MotorTestRobot::new);

// Color Sensor Testing
// RobotBase.startRobot(ColorSensorTestRobot::new);

// Full System
RobotBase.startRobot(Robot::new);
```

## Test 1: Motor Testing

### Purpose
Verify each motor works correctly and spins in the right direction.

### Setup
1. Set Main.java to use `MotorTestRobot::new`
2. Deploy to robot: `./gradlew deploy`
3. Enable teleop mode

### Controls
- **Button 1 (A)** - Test Drive Left Motor
- **Button 2 (B)** - Test Drive Right Motor
- **Button 3 (X)** - Test Intake Motor
- **Button 4 (Y)** - Test Shooter Motor
- **Button 5 (LB)** - Test Reject Motor
- **Button 6 (RB)** - STOP ALL MOTORS
- **D-Pad Up** - Increase speed
- **D-Pad Down** - Decrease speed

### Testing Procedure

1. **Test Drive Left Motor**
   - [ ] Press Button 1 (A)
   - [ ] Verify left drive motor spins
   - [ ] Check direction (should drive robot forward)
   - [ ] Check SmartDashboard current draw
   - [ ] Press Button 6 (RB) to stop

2. **Test Drive Right Motor**
   - [ ] Press Button 2 (B)
   - [ ] Verify right drive motor spins
   - [ ] Check direction (should drive robot forward)
   - [ ] Check SmartDashboard current draw
   - [ ] Press Button 6 (RB) to stop

3. **Test Intake Motor**
   - [ ] Press Button 3 (X)
   - [ ] Verify intake motor spins
   - [ ] Check direction (should pull balls in)
   - [ ] If wrong direction, reverse motor in code or wiring
   - [ ] Press Button 6 (RB) to stop

4. **Test Shooter Motor**
   - [ ] Press Button 4 (Y)
   - [ ] Verify shooter motor spins
   - [ ] Check direction (should send balls to shooter)
   - [ ] If wrong direction, reverse motor in code or wiring
   - [ ] Press Button 6 (RB) to stop

5. **Test Reject Motor**
   - [ ] Press Button 5 (LB)
   - [ ] Verify reject motor spins
   - [ ] Check direction (should eject balls)
   - [ ] If wrong direction, reverse motor in code or wiring
   - [ ] Press Button 6 (RB) to stop

6. **Test Speed Control**
   - [ ] Press D-Pad Up to increase speed
   - [ ] Verify speed increases (check console output)
   - [ ] Press D-Pad Down to decrease speed
   - [ ] Verify speed decreases

### SmartDashboard Values to Monitor
- Test Mode (current motor being tested)
- Test Speed (current speed setting)
- Drive Left Current
- Drive Right Current
- Intake Current
- Shooter Current
- Reject Current

### Fixing Motor Direction

If a motor spins the wrong direction:

**Option 1: In Code** - Negate the speed value
```java
motorName.setControl(new DutyCycleOut(-testSpeed));
```

**Option 2: In Hardware** - Swap motor wires or change motor invert setting

## Test 2: Color Sensor Testing

### Purpose
Verify color sensor detects balls correctly and matches colors accurately.

### Setup
1. Set Main.java to use `ColorSensorTestRobot::new`
2. Deploy to robot: `./gradlew deploy`
3. Enable teleop mode
4. Prepare ball samples of each color

### Controls
- **Button 1 (A)** - Test against Red
- **Button 2 (B)** - Test against Blue
- **Button 3 (X)** - Test against Green
- **Button 4 (Y)** - Test against Yellow
- **Button 5 (LB)** - Save current color as calibration

### Testing Procedure

1. **Basic Detection Test**
   - [ ] Watch SmartDashboard proximity value
   - [ ] Bring ball close to sensor
   - [ ] Verify proximity increases above 100
   - [ ] Verify "Ball Detected" becomes true

2. **Color Matching Test - Red Ball**
   - [ ] Place red ball near sensor
   - [ ] Press Button 1 (A)
   - [ ] Check console: Should show "Expected: Red | Actual: Red"
   - [ ] Check confidence value (should be > 0.8)
   - [ ] If wrong, proceed to calibration

3. **Color Matching Test - Blue Ball**
   - [ ] Place blue ball near sensor
   - [ ] Press Button 2 (B)
   - [ ] Check console: Should show "Expected: Blue | Actual: Blue"
   - [ ] Check confidence value (should be > 0.8)
   - [ ] If wrong, proceed to calibration

4. **Color Matching Test - Green Ball**
   - [ ] Place green ball near sensor
   - [ ] Press Button 3 (X)
   - [ ] Check console: Should show "Expected: Green | Actual: Green"
   - [ ] Check confidence value (should be > 0.8)
   - [ ] If wrong, proceed to calibration

5. **Color Matching Test - Yellow Ball**
   - [ ] Place yellow ball near sensor
   - [ ] Press Button 4 (Y)
   - [ ] Check console: Should show "Expected: Yellow | Actual: Yellow"
   - [ ] Check confidence value (should be > 0.8)
   - [ ] If wrong, proceed to calibration

6. **Calibration (If Needed)**
   - [ ] Place ball of known color near sensor
   - [ ] Wait for stable reading
   - [ ] Press Button 5 (LB)
   - [ ] Note the RGB values printed in console
   - [ ] Update Constants.java with new RGB values

### SmartDashboard Values to Monitor
- Detected Color (color name)
- Confidence (0.0 to 1.0)
- Proximity (distance value)
- Red, Green, Blue (RGB components)
- Ball Detected (boolean)
- Calibrated Color (last saved color)
- Cal Red, Cal Green, Cal Blue (calibrated RGB values)

### Calibrating Colors

If colors are misidentified, calibrate them:

1. Run ColorSensorTestRobot
2. Place ball of known color near sensor
3. Press Button 5 (LB) to save calibration
4. Copy RGB values from console
5. Update Constants.java:

```java
public static final Color RED_TARGET = new Color(0.561, 0.232, 0.114);
```

Replace with your calibrated values.

### Troubleshooting Color Detection

**Problem: Low confidence values**
- Adjust lighting conditions
- Clean color sensor lens
- Lower COLOR_CONFIDENCE_THRESHOLD in Constants.java

**Problem: Wrong colors detected**
- Recalibrate color values
- Check ball material (matte vs glossy affects readings)
- Adjust proximity threshold

**Problem: No ball detected**
- Check proximity threshold (lower it if needed)
- Verify color sensor wiring
- Check I2C connection

## Test 3: Full System Testing

### Purpose
Test complete ball sorting operation end-to-end.

### Setup
1. Set Main.java to use `Robot::new`
2. Set desired target color in Constants.java
3. Deploy to robot: `./gradlew deploy`
4. Enable teleop mode
5. Prepare balls of multiple colors

### Controls
- **A Button (1)** - Enable intake
- **B Button (2)** - Disable intake
- **Left Joystick** - Drive robot

### Testing Procedure

1. **System Initialization**
   - [ ] Check console for "Robot Ready" message
   - [ ] Verify target color is correct
   - [ ] Check SmartDashboard values

2. **Correct Color Ball Test**
   - [ ] Press A button to enable intake
   - [ ] Feed ball of CORRECT color
   - [ ] Verify console shows "Ball detected! Color: [COLOR]"
   - [ ] Verify console shows "✓ Correct color! Routing to SHOOTER"
   - [ ] Verify shooter motor runs
   - [ ] Verify ball goes to shooting position
   - [ ] Verify system returns to IDLE
   - [ ] Verify console shows "Ready for next ball!"

3. **Wrong Color Ball Test**
   - [ ] Feed ball of WRONG color
   - [ ] Verify console shows "Ball detected! Color: [COLOR]"
   - [ ] Verify console shows "✗ Wrong color! Routing to REJECT"
   - [ ] Verify reject motor runs
   - [ ] Verify ball is ejected
   - [ ] Verify system returns to IDLE

4. **Multiple Ball Test**
   - [ ] Feed correct color ball
   - [ ] Wait for "Ready for next ball!"
   - [ ] Feed wrong color ball
   - [ ] Wait for "Ready for next ball!"
   - [ ] Feed correct color ball again
   - [ ] Verify all balls processed correctly

5. **Disable Test**
   - [ ] Press B button to disable intake
   - [ ] Verify intake stops
   - [ ] Verify no balls are processed
   - [ ] Press A button to re-enable

6. **Drive Test with Sorting**
   - [ ] Enable intake (A button)
   - [ ] Drive robot using joystick
   - [ ] Feed balls while driving
   - [ ] Verify sorting works while driving

### SmartDashboard Values to Monitor
- Sorting State (current state machine state)
- Ball Detected (boolean)
- Last Color (last detected ball color)
- Proximity (current proximity value)
- Red, Green, Blue (current RGB values)
- Intake Enabled (boolean)
- Target Color (configured target)

### Adjusting System Parameters

If system doesn't work correctly:

**Problem: Balls not routing correctly**
- Increase SORT_DURATION_MS for more routing time
- Adjust motor speeds

**Problem: System jams**
- Increase BALL_CLEAR_TIME_MS
- Adjust motor directions
- Check mechanical alignment

**Problem: False detections**
- Increase PROXIMITY_THRESHOLD
- Increase COLOR_CONFIDENCE_THRESHOLD

**Problem: Missed detections**
- Decrease PROXIMITY_THRESHOLD
- Decrease COLOR_CONFIDENCE_THRESHOLD

## Final Competition Checklist

- [ ] All motors spin correct direction
- [ ] Color sensor calibrated for field lighting
- [ ] Target color set correctly
- [ ] All timing parameters tuned
- [ ] Drive controls working smoothly
- [ ] Multiple ball sorting tested successfully
- [ ] Enable/disable buttons working
- [ ] SmartDashboard telemetry verified
- [ ] Battery fully charged
- [ ] All wiring secured
- [ ] Code deployed and tested on competition field
- [ ] Driver trained on controls
- [ ] Backup code ready

## Quick Reference - Button Mappings

### Motor Test Mode
| Button | Function |
|--------|----------|
| A (1) | Test Drive Left |
| B (2) | Test Drive Right |
| X (3) | Test Intake |
| Y (4) | Test Shooter |
| LB (5) | Test Reject |
| RB (6) | Stop All |
| D-Pad Up | Increase Speed |
| D-Pad Down | Decrease Speed |

### Color Test Mode
| Button | Function |
|--------|----------|
| A (1) | Test Red |
| B (2) | Test Blue |
| X (3) | Test Green |
| Y (4) | Test Yellow |
| LB (5) | Save Calibration |

### Full System Mode
| Button | Function |
|--------|----------|
| A (1) | Enable Intake |
| B (2) | Disable Intake |
| Left Stick | Drive Robot |

## Contact & Support

For issues during testing:
1. Check console output for error messages
2. Verify SmartDashboard values
3. Review this testing guide
4. Check wiring and connections
5. Verify CAN IDs match Constants.java

Good luck testing your robot!
