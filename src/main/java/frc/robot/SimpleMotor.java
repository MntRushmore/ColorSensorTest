package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

/**
 * SIMPLEST POSSIBLE MOTOR CONTROL
 * 
 * Hardware: Kraken X50 on CAN ID 1
 * 
 * Controls:
 * Y Button = Run at 0.7
 * X Button = Run at 0.8
 * B Button = Run at 0.75
 * A Button = Stop (0.0)
 */
public class SimpleMotor extends TimedRobot {
    
    private TalonFX motor;
    private XboxController controller;
    
    @Override
    public void robotInit() {
        System.out.println("\n\n=== SIMPLE MOTOR CONTROL ===");
        System.out.println("Motor CAN ID: 1");
        System.out.println("Y=0.7 | X=0.8 | B=0.75 | A=Stop");
        System.out.println("============================\n");
        
        motor = new TalonFX(1);  // CAN ID 1
        controller = new XboxController(0);
    }
    
    @Override
    public void teleopPeriodic() {
        double speed = 0.0;
        
        if (controller.getYButton()) {
            speed = 0.7;
        } else if (controller.getXButton()) {
            speed = 0.8;
        } else if (controller.getBButton()) {
            speed = 0.75;
        } else if (controller.getAButton()) {
            speed = 0.0;
        }
        
        motor.setControl(new DutyCycleOut(speed));
    }
    
    @Override
    public void disabledInit() {
        motor.setControl(new DutyCycleOut(0.0));
    }
}
