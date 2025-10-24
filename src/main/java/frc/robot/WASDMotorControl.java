package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

/**
 * SUPER SIMPLE WASD Motor Control for Kraken X50
 * 
 * WINDOWS COMPUTER SETUP:
 * 1. In Driver Station, set up Xbox Controller as Joystick 0
 * 2. Use WASD keys on keyboard OR Xbox controller buttons
 * 
 * CONTROLS:
 * W or Y Button = Intake (0.7)
 * A or X Button = Outtake 1 (0.8)
 * S or B Button = Outtake 2 (0.75)
 * D or A Button = Stop Motor
 * 
 * MOTOR: Kraken X50 on CAN ID 1
 */
public class WASDMotorControl extends TimedRobot {
    
    // Motor on CAN ID 1 (Kraken X50)
    private static final int MOTOR_CAN_ID = 1;
    
    private TalonFX motor;
    private XboxController controller;
    
    private double currentSpeed = 0.0;
    private String currentMode = "STOPPED";
    
    private int loopCount = 0;
    
    @Override
    public void robotInit() {
        System.out.println("\n\n");
        System.out.println("========================================");
        System.out.println("    WASD MOTOR CONTROL - KRAKEN X50");
        System.out.println("========================================");
        System.out.println("");
        System.out.println("CONTROLS:");
        System.out.println("  W or Y Button = Intake (0.7)");
        System.out.println("  A or X Button = Outtake 1 (0.8)");
        System.out.println("  S or B Button = Outtake 2 (0.75)");
        System.out.println("  D or A Button = Stop Motor");
        System.out.println("");
        System.out.println("========================================");
        
        // Initialize motor on CAN ID 1
        motor = new TalonFX(MOTOR_CAN_ID);
        controller = new XboxController(0);
        
        System.out.println("✅ Motor CAN ID: " + MOTOR_CAN_ID);
        
        try {
            double temp = motor.getDeviceTemp().getValueAsDouble();
            System.out.println("✅ Kraken X50 detected!");
            System.out.println("   Temperature: " + String.format("%.1f", temp) + "°C");
        } catch (Exception e) {
            System.out.println("⚠️  WARNING: Cannot read motor status!");
            System.out.println("   Check: Motor power, CAN wiring, CAN ID");
        }
        
        System.out.println("");
        System.out.println("🎮 Ready! Press W, A, S, or D");
        System.out.println("\n\n");
    }
    
    @Override
    public void teleopPeriodic() {
        loopCount++;
        
        // Check for WASD or Xbox buttons
        if (controller.getYButton()) {  // Y or W
            if (!currentMode.equals("INTAKE")) {
                currentSpeed = 0.7;
                currentMode = "INTAKE";
                System.out.println("\n>>> W PRESSED - INTAKE MODE (0.7) <<<\n");
            }
        }
        else if (controller.getXButton()) {  // X or A
            if (!currentMode.equals("OUTTAKE_1")) {
                currentSpeed = 0.8;
                currentMode = "OUTTAKE_1";
                System.out.println("\n>>> A PRESSED - OUTTAKE 1 MODE (0.8) <<<\n");
            }
        }
        else if (controller.getBButton()) {  // B or S
            if (!currentMode.equals("OUTTAKE_2")) {
                currentSpeed = 0.75;
                currentMode = "OUTTAKE_2";
                System.out.println("\n>>> S PRESSED - OUTTAKE 2 MODE (0.75) <<<\n");
            }
        }
        else if (controller.getAButton()) {  // A or D
            if (!currentMode.equals("STOPPED")) {
                currentSpeed = 0.0;
                currentMode = "STOPPED";
                System.out.println("\n>>> D PRESSED - MOTOR STOPPED <<<\n");
            }
        }
        
        // Send speed to motor
        motor.setControl(new DutyCycleOut(currentSpeed));
        
        // Print status every 1 second (50 loops)
        if (loopCount % 50 == 0) {
            System.out.println("══════════════════════════════════════");
            System.out.println("  MODE: " + currentMode);
            System.out.println("  SPEED: " + currentSpeed);
            System.out.println("  CURRENT: " + String.format("%.2f", motor.getSupplyCurrent().getValueAsDouble()) + " A");
            System.out.println("  TEMP: " + String.format("%.1f", motor.getDeviceTemp().getValueAsDouble()) + "°C");
            System.out.println("══════════════════════════════════════");
        }
    }
    
    @Override
    public void disabledInit() {
        motor.setControl(new DutyCycleOut(0.0));
        currentSpeed = 0.0;
        currentMode = "DISABLED";
        System.out.println("\n⛔ Robot Disabled - Motor Stopped\n");
    }
    
    @Override
    public void teleopInit() {
        System.out.println("\n✅ TeleOp Enabled - Press WASD keys!\n");
    }
}
