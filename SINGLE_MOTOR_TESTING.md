# Single Motor Testing Guide

## Overview

This test mode is designed for testing your controller mapping with **ONLY ONE PHYSICAL MOTOR**. 

Since you mentioned you only have 1 motor right now, this mode lets you test all your button mappings by running that single motor at different speeds based on which button you press.

## Hardware Setup

### Required Hardware
- 1 TalonFX/Kraken motor connected to **CAN ID 3** (Intake motor port)
- Xbox/PS4 controller connected to Driver Station
- No color sensor needed for this test

### Optional for Future
- REV Color Sensor V3 (for automatic sorting later)

## Controller Button Mapping

| Button | Function | Speed | Purpose |
|--------|----------|-------|---------|
| **A (Button 1)** | Fast Intake | 0.7 | Pull balls in quickly |
| **B (Button 2)** | Outtake 1 (Shooter) | 0.8 | Send balls to shooter |
| **X (Button 3)** | Outtake 2 (Reject) | 0.75 | Eject wrong color balls |
| **Y (Button 4)** | **STOP** | 0.0 | Stop motor completely |
| **D-Pad Up** | Increase Speed | +0.05 | Fine-tune current mode speed |
| **D-Pad Down** | Decrease Speed | -0.05 | Fine-tune current mode speed |

## How to Use

### 1. Deploy to Robot
```bash
./gradlew deploy
```

### 2. Enable TeleOp Mode
Enable your robot in TeleOp mode from the Driver Station

### 3. Test Each Function

#### Test Intake (Fast Speed)
1. Press **A Button**
2. Motor should spin at fast speed (0.7)
3. This simulates pulling balls into the system
4. Press **Y Button** to stop

#### Test Outtake 1 (Shooter)
1. Press **B Button**
2. Motor should spin at high speed (0.8)
3. This simulates sending balls to the shooter
4. Press **Y Button** to stop

#### Test Outtake 2 (Reject)
1. Press **X Button**
2. Motor should spin at medium-high speed (0.75)
3. This simulates ejecting wrong color balls
4. Press **Y Button** to stop

#### Adjust Speeds On-The-Fly
1. Press **A**, **B**, or **X** to select a mode
2. While motor is running, press **D-Pad Up** to increase speed
3. Press **D-Pad Down** to decrease speed
4. Watch the console for speed updates
5. Speeds are clamped between 0.0 and 1.0

## SmartDashboard Values

Monitor these values on SmartDashboard:

| Value | Description |
|-------|-------------|
| Motor Mode | Current mode (INTAKE, OUTTAKE_1, OUTTAKE_2, STOPPED) |
| Current Speed | Actual speed being sent to motor |
| Intake Speed | Configured intake speed |
| Outtake 1 Speed | Configured outtake 1 speed |
| Outtake 2 Speed | Configured outtake 2 speed |
| Motor Current (A) | Current draw in amps |
| Motor Temperature (C) | Motor temperature |
| A/B/X/Y Button | Button states (true/false) |

## Testing Checklist

- [ ] Motor spins when A button pressed (intake)
- [ ] Motor spins when B button pressed (outtake 1)
- [ ] Motor spins when X button pressed (outtake 2)
- [ ] Motor stops when Y button pressed
- [ ] Speed increases with D-Pad Up
- [ ] Speed decreases with D-Pad Down
- [ ] Controller feels responsive and natural
- [ ] Button layout makes sense for your needs
- [ ] SmartDashboard shows correct values
- [ ] Motor direction is correct for each function

## Motor Direction

If the motor spins the wrong direction for any function:

### Option 1: Reverse in Code
Edit `SingleMotorTestRobot.java` and negate the speed:
```java
motorSpeed = -motorSpeed; // Add negative sign
```

### Option 2: Invert Motor
Add motor invert in `robotInit()`:
```java
testMotor.setInverted(true);
```

### Option 3: Hardware
Swap the motor wires (not recommended, use software)

## Customizing Button Layout

Want different buttons? Edit `SingleMotorTestRobot.java`:

```java
if (controller.getRawButtonPressed(1)) { // Change button number
    currentMode = MotorMode.INTAKE;
    System.out.println(">>> INTAKE MODE");
}
```

Xbox Controller Button Numbers:
- A = 1
- B = 2
- X = 3
- Y = 4
- LB = 5
- RB = 6
- Back = 7
- Start = 8
- Left Stick = 9
- Right Stick = 10

## Customizing Speeds

Want different default speeds? Edit the initialization in `SingleMotorTestRobot.java`:

```java
private double intakeSpeed = 0.7;   // Change this
private double outtake1Speed = 0.8;  // Change this
private double outtake2Speed = 0.75; // Change this
```

Or adjust them live with D-Pad Up/Down during testing!

## Next Steps

### When You Get More Motors

Once you have all 3 motors installed:

1. Switch to `MotorTestRobot` to test each motor individually
2. Use `TESTING_GUIDE.md` for full motor testing
3. Eventually switch to full `Robot` mode for automatic sorting

### To Switch Modes

Edit `src/main/java/frc/robot/Main.java`:

```java
// Single motor testing (CURRENT)
RobotBase.startRobot(SingleMotorTestRobot::new);

// All motor testing
// RobotBase.startRobot(MotorTestRobot::new);

// Full system with color sensor
// RobotBase.startRobot(Robot::new);
```

## Troubleshooting

### Motor doesn't spin
- Check CAN ID is correct (should be 3)
- Verify motor is connected and powered
- Check motor controller is not in a fault state
- Enable robot in Driver Station

### Wrong button does nothing
- Verify controller is connected to DS
- Check button numbers match your controller
- Watch console output for button press messages

### Speed too fast/slow
- Use D-Pad Up/Down to adjust
- Or edit default speeds in code
- Remember: 1.0 = 100%, 0.5 = 50%, etc.

### Controller not responding
- Reconnect controller to Driver Station
- Check USB connection
- Verify controller port is 0 in Driver Station

## Color Sensor (Future)

The color sensor is already configured in your full `Robot.java` code. When you're ready:

1. Install REV Color Sensor V3 on I2C port
2. Switch to `Robot::new` in Main.java
3. Follow `BALL_SORTING_README.md` for automatic sorting
4. The sensor will detect ball colors automatically
5. System will route balls based on color

For now, focus on getting your controller mapping perfect!

## Tips

1. **Start Slow**: Begin with lower speeds and increase gradually
2. **Test Each Function**: Make sure each button does what you expect
3. **Practice Transitions**: Practice switching between modes quickly
4. **Check Direction**: Verify motor direction is correct for each function
5. **Monitor Current**: Watch motor current to ensure it's not stalling
6. **Temperature**: Keep an eye on motor temperature during extended testing

## Competition Ready Checklist

Before competition, make sure:
- [ ] All button mappings feel natural to driver
- [ ] Motor directions are correct
- [ ] Speeds are optimized for your mechanism
- [ ] Driver has practiced all button combinations
- [ ] Backup controller tested and ready
- [ ] Code deployed and verified on field

## Questions?

Check out the other guides:
- `TESTING_GUIDE.md` - Full multi-motor testing
- `BALL_SORTING_README.md` - Automatic color sorting system
- `README.md` - Project overview (if available)

Happy testing! 🤖⚡
