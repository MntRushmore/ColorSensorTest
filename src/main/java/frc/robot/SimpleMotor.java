package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.sim.TalonFXSimState;

/**
 * SIMPLEST POSSIBLE MOTOR CONTROL - SIMULATION VERSION
 * 
 * Hardware: Kraken X50 on CAN ID 1
 * 
 * Keyboard Controls (using Joystick 0):
 * Button 1 = Run at 0.7
 * Button 2 = Run at 0.8
 * Button 3 = Run at 0.75
 * Button 4 = Stop (0.0)
 * 
 * Note: In FRC Driver Station with keyboard, buttons map to number keys
 */
public class Robot extends TimedRobot {
    
    private TalonFX motor;
    private Joystick keyboard;
    private DutyCycleOut dutyCycleControl;
    private TalonFXSimState motorSim;
    
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
    
    @Override
    public void simulationPeriodic() {
        // Update motor simulation
        motorSim.setSupplyVoltage(12.0);  // Simulated battery voltage
        
        // Simulate motor physics (simple model)
        // In a real sim, you'd calculate velocity based on voltage and load
        double appliedVoltage = motorSim.getMotorVoltage();
        double simulatedVelocity = appliedVoltage * 100.0;  // Simplified: 100 RPS per volt
        
        motorSim.setRawRotorPosition(motorSim.getRawRotorPosition() + simulatedVelocity * 0.020);  // 20ms periodic
        motorSim.setRotorVelocity(simulatedVelocity);
        
        // Optional: Print simulation data periodically
        if (Math.abs(appliedVoltage) > 0.1) {
            System.out.println(String.format("SIM - Voltage: %.2fV, Velocity: %.1f RPS", 
                appliedVoltage, simulatedVelocity));
        }
    }
}