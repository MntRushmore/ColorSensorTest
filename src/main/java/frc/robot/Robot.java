package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;

public class Robot extends TimedRobot {

    // --- Color Sensor ---
    private final I2C.Port i2cPort = I2C.Port.kOnboard; // use kMXP if on MXP
    private ColorSensorV3 colorSensor;
    private double lastPrintTime = 0;
    private static final double PRINT_INTERVAL = 0.5; // Print every 0.5 seconds

    // --- Motor Control ---
    private TalonFX motor;
    private Joystick keyboard;
    private DutyCycleOut dutyCycleControl;
    private TalonFXSimState motorSim;

    // --- Robot Initialization ---
    @Override
    public void robotInit() {
        System.out.println("\n\n=== SIMPLE MOTOR CONTROL - SIMULATION ===");
        System.out.println("Motor CAN ID: 1");
        System.out.println("Button 1=0.7 | Button 2=0.8 | Button 3=0.75 | Button 4=Stop");
        System.out.println("=========================================\n");

        // Try to initialize motor, but don't crash if it fails
        try {
            motor = new TalonFX(1);  // CAN ID 1
            keyboard = new Joystick(0);  // Keyboard shows as Joystick 0
            dutyCycleControl = new DutyCycleOut(0.0);
            motorSim = motor.getSimState();
            System.out.println("Motor initialized successfully");
        } catch (Exception e) {
            System.out.println("WARNING: Motor not connected - motor control disabled");
            System.out.println("Error: " + e.getMessage());
        }

        // Only initialize color sensor on real robot
        if (RobotBase.isReal()) {
            try {
                colorSensor = new ColorSensorV3(i2cPort);
                System.out.println("Color sensor initialized");
            } catch (Exception e) {
                System.out.println("WARNING: Color sensor not connected");
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Simulation mode - Color sensor disabled");
        }
    }

    // --- Runs every robot loop ---
    @Override
    public void robotPeriodic() {
        // Only read color sensor on real robot
        if (RobotBase.isReal() && colorSensor != null) {
            double currentTime = Timer.getFPGATimestamp();
            
            // Only print every PRINT_INTERVAL seconds to avoid spam
            if (currentTime - lastPrintTime >= PRINT_INTERVAL) {
                lastPrintTime = currentTime;
                
                try {
                    var color = colorSensor.getColor();
                    int proximity = colorSensor.getProximity();
                    
                    // Determine dominant color
                    String detectedColor = "Unknown";
                    double maxValue = Math.max(color.red, Math.max(color.green, color.blue));
                    
                    if (maxValue < 0.2) {
                        detectedColor = "Black/Dark";
                    } else if (color.red > color.green && color.red > color.blue) {
                        if (color.green > 0.3 && color.blue < 0.3) {
                            detectedColor = "Yellow";
                        } else {
                            detectedColor = "Red";
                        }
                    } else if (color.green > color.red && color.green > color.blue) {
                        detectedColor = "Green";
                    } else if (color.blue > color.red && color.blue > color.green) {
                        detectedColor = "Blue";
                    } else if (color.red > 0.4 && color.green > 0.4 && color.blue > 0.4) {
                        detectedColor = "White";
                    }
                    
                    System.out.println("========================================");
                    System.out.printf("COLOR SENSOR: %s%n", detectedColor);
                    System.out.printf("R=%.3f  G=%.3f  B=%.3f  Proximity=%d%n",
                            color.red, color.green, color.blue, proximity);
                    System.out.println("========================================");
                } catch (Exception e) {
                    System.out.println("ERROR reading color sensor: " + e.getMessage());
                }
            }
        }
    }

    // --- Teleop motor control ---
    @Override
    public void teleopPeriodic() {
        // Only control motor if it's connected
        if (motor == null || keyboard == null) {
            return;
        }

        double speed = 0.0;

        if (keyboard.getRawButton(1)) {
            speed = 0.7;
            System.out.println("Button 1 pressed - Speed: 0.7");
        } else if (keyboard.getRawButton(2)) {
            speed = 0.8;
            System.out.println("Button 2 pressed - Speed: 0.8");
        } else if (keyboard.getRawButton(3)) {
            speed = 0.75;
            System.out.println("Button 3 pressed - Speed: 0.75");
        } else if (keyboard.getRawButton(4)) {
            speed = 0.0;
            System.out.println("Button 4 pressed - STOP");
        }

        dutyCycleControl.Output = speed;
        motor.setControl(dutyCycleControl);
    }

    // --- Disabled ---
    @Override
    public void disabledInit() {
        // Only stop motor if it's connected
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
        // Only stop motor if it's connected
        if (motor != null && dutyCycleControl != null) {
            dutyCycleControl.Output = 0.0;
            motor.setControl(dutyCycleControl);
        }
    }

    // --- Simulation ---
    @Override
    public void simulationPeriodic() {
        // Only simulate motor if it's connected
        if (motorSim == null) {
            return;
        }

        // Update motor simulation
        motorSim.setSupplyVoltage(12.0);  // Simulated battery voltage

        double appliedVoltage = motorSim.getMotorVoltage();
        double simulatedVelocity = appliedVoltage * 100.0;  // Simplified model

        motorSim.addRotorPosition(simulatedVelocity * 0.020);  // 20ms periodic
        motorSim.setRotorVelocity(simulatedVelocity);

        if (Math.abs(appliedVoltage) > 0.1) {
            System.out.println(String.format("SIM - Voltage: %.2fV, Velocity: %.1f RPS",
                    appliedVoltage, simulatedVelocity));
        }
    }
}