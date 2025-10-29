package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PS4Controller;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;

public class Robot extends TimedRobot {

    // --- Color Sensor ---
    private final I2C.Port i2cPort = I2C.Port.kOnboard; // Change to kMXP if sensor is on MXP port
    private ColorSensorV3 colorSensor;
    private double lastPrintTime = 0;
    private static final double PRINT_INTERVAL = 0.5; // Print every 0.5 seconds

    // --- Motor Control ---
    private TalonFX motor;
    private DutyCycleOut dutyCycleControl;
    private TalonFXSimState motorSim;

    // --- PS4 Controller ---
    private PS4Controller controller;

    @Override
public void robotInit() {
    System.out.println("RIOLOG TEST MESSAGE");
    System.out.println("\n\n=== SIMPLE MOTOR CONTROL - SIMULATION ===");
    System.out.println("Motor CAN ID: 1 on CANivore");
    System.out.println("Cross=0.7 | Circle=0.8 | Square=0.75 | Triangle=Stop");
    System.out.println("=========================================\n");

    // Check if we're on real robot or simulation
    if (RobotBase.isReal()) {
        System.out.println("*** RUNNING ON REAL ROBOT ***");
    } else {
        System.out.println("*** RUNNING IN SIMULATION ***");
    }

    // Initialize motor on CANivore bus
    try {
        motor = new TalonFX(1, "canivore");  // CAN ID 1 on CANivore bus
        dutyCycleControl = new DutyCycleOut(0.0);
        motorSim = motor.getSimState();
        System.out.println("✓ Motor initialized successfully on CANivore");
    } catch (Exception e) {
        System.out.println("✗ WARNING: Motor not connected - motor control disabled");
        System.out.println("  Error: " + e.getMessage());
    }

    // Initialize PS4 controller on USB port 0
    controller = new PS4Controller(0); 
    System.out.println("✓ PS4 controller initialized on USB port 0");

    // Initialize color sensor
    System.out.println("\nAttempting to initialize color sensor...");
    System.out.println("Using I2C port: " + i2cPort);
    try {
        colorSensor = new ColorSensorV3(i2cPort);
        System.out.println("✓ Color sensor object created");

        if (!colorSensor.isConnected()) {
            System.out.println("✗ WARNING: Sensor reports NOT CONNECTED!");
        } else {
            System.out.println("✓ Sensor reports CONNECTED");
        }

        var testColor = colorSensor.getColor();
        System.out.printf("✓ Initial reading: R=%.3f G=%.3f B=%.3f%n", 
                          testColor.red, testColor.green, testColor.blue);

        if (Double.isNaN(testColor.red)) {
            System.out.println("✗ WARNING: Getting NaN values - I2C communication failure!");
        }
    } catch (Exception e) {
        System.out.println("✗ WARNING: Color sensor initialization failed!");
        e.printStackTrace();
        colorSensor = null;
    }

    System.out.println("\n=== INITIALIZATION COMPLETE ===\n");
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

                System.out.println("========================================");
                System.out.printf("COLOR SENSOR: %s%n", detectedColor);
                System.out.printf("R=%.3f  G=%.3f  B=%.3f  Proximity=%d%n",
                                  color.red, color.green, color.blue, proximity);
                System.out.println("========================================");
                System.out.flush();
            } catch (Exception e) {
                System.out.println("✗ ERROR reading color sensor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void teleopPeriodic() {
        if (motor == null || controller == null) return;

        double speed = 0.0;

        if (controller.getCrossButton()) {        // Cross = A
            speed = 0.7;
            System.out.println("Cross pressed - Speed: 0.7");
        } else if (controller.getCircleButton()) { // Circle = B
            speed = 0.8;
            System.out.println("Circle pressed - Speed: 0.8");
        } else if (controller.getSquareButton()) { // Square = X
            speed = 0.75;
            System.out.println("Square pressed - Speed: 0.75");
        } else if (controller.getTriangleButton()) { // Triangle = Y
            speed = 0.0;
            System.out.println("Triangle pressed - STOP");
        }

        dutyCycleControl.Output = speed;
        motor.setControl(dutyCycleControl);
    }

    @Override
    public void disabledInit() {
        if (motor != null && dutyCycleControl != null) {
            dutyCycleControl.Output = 0.0;
            motor.setControl(dutyCycleControl);
            System.out.println("Robot disabled - Motor stopped");
        } else {
            System.out.println("Robot disabled");
        }
    }

    @Override
    public void disabledPeriodic() {
        if (motor != null && dutyCycleControl != null) {
            dutyCycleControl.Output = 0.0;
            motor.setControl(dutyCycleControl);
        }
    }

    @Override
    public void simulationPeriodic() {
        if (motorSim == null) return;

        motorSim.setSupplyVoltage(12.0);
        double appliedVoltage = motorSim.getMotorVoltage();
        double simulatedVelocity = appliedVoltage * 100.0;
        motorSim.addRotorPosition(simulatedVelocity * 0.020);
        motorSim.setRotorVelocity(simulatedVelocity);

        if (Math.abs(appliedVoltage) > 0.1) {
            System.out.println(String.format("SIM - Voltage: %.2fV, Velocity: %.1f RPS",
                    appliedVoltage, simulatedVelocity));
        }
    }
}