package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PS4Controller;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;

public class Robot extends TimedRobot {

    // --- Color Sensor ---
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private ColorSensorV3 colorSensor;
    private double lastPrintTime = 0;
    private static final double PRINT_INTERVAL = 0.5;

    // --- Motor Control ---
    private TalonFX motor;
    private DutyCycleOut dutyCycleControl;

    // --- PS4 Controller ---
    private PS4Controller controller;

    @Override
    public void robotInit() {
        if (RobotBase.isReal()) {
            System.out.println("*** RUNNING ON REAL ROBOT ***");
        } else {
            System.out.println("*** RUNNING IN SIMULATION ***");
        }

        // Initialize motor on CANivore bus
        try {
            motor = new TalonFX(1, "canivore");
            dutyCycleControl = new DutyCycleOut(0.0);
            System.out.println("✓ Motor initialized successfully on CANivore");
        } catch (Exception e) {
            System.out.println("✗ WARNING: Motor not connected - motor control disabled");
            System.out.println("  Error: " + e.getMessage());
            motor = null;
        }

        // Initialize PS4 controller
        try {
            controller = new PS4Controller(0);
            System.out.println("✓ PS4 controller initialized on USB port 0");
        } catch (Exception e) {
            System.out.println("✗ WARNING: PS4 controller initialization failed");
            System.out.println("  Error: " + e.getMessage());
            controller = null;
        }

        // Initialize color sensor
        try {
            colorSensor = new ColorSensorV3(i2cPort);
            System.out.println("✓ Color sensor object created");

            if (!colorSensor.isConnected()) {
                System.out.println("✗ WARNING: Sensor reports NOT CONNECTED!");
            } else {
                System.out.println("✓ Sensor reports CONNECTED");
            }
        } catch (Exception e) {
            System.out.println("✗ WARNING: Color sensor initialization failed!");
            e.printStackTrace();
            colorSensor = null;
        }

        System.out.println("\n=== INITIALIZATION COMPLETE ===\n");
    }

    @Override
    public void teleopPeriodic() {
        if (motor == null || controller == null) {
            System.out.println("✗ Motor or controller not initialized. Skipping teleopPeriodic.");
            return;
        }

        double speed = 0.0;

        if (controller.getCrossButton()) {
            speed = 0.7;
            System.out.println("Cross pressed - Speed: 0.7");
        } else if (controller.getCircleButton()) {
            speed = 0.8;
            System.out.println("Circle pressed - Speed: 0.8");
        } else if (controller.getSquareButton()) {
            speed = 0.75;
            System.out.println("Square pressed - Speed: 0.75");
        } else if (controller.getTriangleButton()) {
            speed = 0.0;
            System.out.println("Triangle pressed - STOP");
        }

        // Set motor speed
        dutyCycleControl = new DutyCycleOut(speed);
        motor.set(speed);
    }

    @Override
    public void robotPeriodic() {
        double currentTime = Timer.getFPGATimestamp();

        if (colorSensor == null) {
            if (currentTime - lastPrintTime >= 2.0) {
                lastPrintTime = currentTime;
                System.out.println("⚠ Color sensor is NULL - not initialized properly");
            }
            return;
        }

        if (currentTime - lastPrintTime >= PRINT_INTERVAL) {
            lastPrintTime = currentTime;

            try {
                var color = colorSensor.getColor();
                int proximity = colorSensor.getProximity();

                String detectedColor = "Unknown";
                double maxValue = Math.max(color.red, Math.max(color.green, color.blue));

                if (maxValue < 0.2) {
                    detectedColor = "Black/Dark";
                } else if (color.red > color.green && color.red > color.blue) {
                    if (color.green > 0.3 && color.blue < 0.3) detectedColor = "Yellow";
                    else detectedColor = "Red";
                } else if (color.green > color.red && color.green > color.blue) detectedColor = "Green";
                else if (color.blue > color.red && color.blue > color.green) detectedColor = "Blue";
                else if (color.red > 0.4 && color.green > 0.4 && color.blue > 0.4) detectedColor = "White";
            } catch (Exception e) {
                System.out.println("✗ ERROR reading color sensor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}