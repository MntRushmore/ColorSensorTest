package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;

public class Robot extends TimedRobot {

    // --- Color Sensor ---
    private final I2C.Port i2cPort = I2C.Port.kOnboard; // use kMXP if on MXP
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

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

        motor = new TalonFX(1);  // CAN ID 1
        keyboard = new Joystick(0);  // Keyboard shows as Joystick 0
        dutyCycleControl = new DutyCycleOut(0.0);

        // Get simulation state for the motor
        motorSim = motor.getSimState();
    }

    // --- Runs every robot loop ---
    @Override
    public void robotPeriodic() {
        // Color sensor test
        var color = colorSensor.getColor();
        System.out.printf("R=%.3f  G=%.3f  B=%.3f%n",
                color.red, color.green, color.blue);
    }

    // --- Teleop motor control ---
    @Override
    public void teleopPeriodic() {
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
        dutyCycleControl.Output = 0.0;
        motor.setControl(dutyCycleControl);
        System.out.println("Robot disabled - Motor stopped");
    }

    @Override
    public void disabledPeriodic() {
        dutyCycleControl.Output = 0.0;
        motor.setControl(dutyCycleControl);
    }

    // --- Simulation ---
    @Override
    public void simulationPeriodic() {
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