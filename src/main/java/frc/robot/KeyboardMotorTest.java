package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Keyboard Motor Test Robot
 * 
 * USE A KEYBOARD AS JOYSTICK:
 * Configure Driver Station to use "Keyboard" as controller
 * 
 * NUMBER KEY controls:
 * 1 = Fast Intake (0.7)
 * 2 = Outtake 1 (0.8) 
 * 3 = Outtake 2 (0.75)
 * 4 = Stop Motor
 * 
 * Map keyboard number keys 1-4 to joystick buttons 1-4 in Driver Station
 */
public class KeyboardMotorTest extends TimedRobot {
    
    // Motor CAN ID (0 or 1)
    private static final int MOTOR_CAN_ID = 0;
    
    private TalonFX testMotor;
    
    private enum MotorMode {
        STOPPED,
        INTAKE,
        OUTTAKE_1,
        OUTTAKE_2
    }
    
    private MotorMode currentMode = MotorMode.STOPPED;
    
    private double intakeSpeed = 0.7;
    private double outtake1Speed = 0.8;
    private double outtake2Speed = 0.75;
    
    private int loopCounter = 0;
    
    @Override
    public void robotInit() {
        System.out.println("========================================");
        System.out.println("=== KEYBOARD MOTOR TEST MODE ===");
        System.out.println("========================================");
        System.out.println("SETUP INSTRUCTIONS:");
        System.out.println("1. In Driver Station, add 'Keyboard' as joystick");
        System.out.println("2. Map number keys 1-4 to buttons");
        System.out.println("");
        System.out.println("Keyboard Controls (Number Keys):");
        System.out.println("  1 = Fast Intake (0.7)");
        System.out.println("  2 = Outtake 1 (0.8)");
        System.out.println("  3 = Outtake 2 (0.75)");
        System.out.println("  4 = Stop Motor");
        System.out.println("========================================");
        
        testMotor = new TalonFX(MOTOR_CAN_ID);
        
        System.out.println("Motor initialized on CAN ID: " + MOTOR_CAN_ID);
        
        try {
            double temp = testMotor.getDeviceTemp().getValueAsDouble();
            System.out.println("✅ Motor detected! Temp: " + temp + "°C");
        } catch (Exception e) {
            System.out.println("⚠️  WARNING: Could not detect motor!");
        }
        
        System.out.println("\n🎹 Press 1, 2, 3, or 4 on your keyboard!");
        System.out.println("Ready!\n");
    }
    
    @Override
    public void teleopPeriodic() {
        loopCounter++;
        
        // Read keyboard as joystick (buttons 1-4)
        // In Driver Station, keyboard keys map to joystick buttons
        edu.wpi.first.wpilibj.Joystick keyboard = new edu.wpi.first.wpilibj.Joystick(0);
        
        double motorSpeed = 0.0;
        String status = "STOPPED";
        
        // Check button presses
        if (keyboard.getRawButton(1)) {  // Key 1
            motorSpeed = intakeSpeed;
            status = "INTAKE at " + intakeSpeed;
            if (currentMode != MotorMode.INTAKE) {
                System.out.println(">>> ✅ KEY 1 PRESSED - INTAKE MODE");
                currentMode = MotorMode.INTAKE;
            }
        }
        else if (keyboard.getRawButton(2)) {  // Key 2
            motorSpeed = outtake1Speed;
            status = "OUTTAKE 1 at " + outtake1Speed;
            if (currentMode != MotorMode.OUTTAKE_1) {
                System.out.println(">>> ✅ KEY 2 PRESSED - OUTTAKE 1 MODE");
                currentMode = MotorMode.OUTTAKE_1;
            }
        }
        else if (keyboard.getRawButton(3)) {  // Key 3
            motorSpeed = outtake2Speed;
            status = "OUTTAKE 2 at " + outtake2Speed;
            if (currentMode != MotorMode.OUTTAKE_2) {
                System.out.println(">>> ✅ KEY 3 PRESSED - OUTTAKE 2 MODE");
                currentMode = MotorMode.OUTTAKE_2;
            }
        }
        else if (keyboard.getRawButton(4)) {  // Key 4
            motorSpeed = 0.0;
            status = "STOPPED";
            if (currentMode != MotorMode.STOPPED) {
                System.out.println(">>> ⛔ KEY 4 PRESSED - MOTOR STOPPED");
                currentMode = MotorMode.STOPPED;
            }
        }
        
        // Send command to motor
        testMotor.setControl(new DutyCycleOut(motorSpeed));
        
        // Print status every 50 loops (~1 second)
        if (loopCounter % 50 == 0) {
            System.out.println("────────────────────────────────────────");
            System.out.println("📊 Mode: " + status);
            System.out.println("⚡ Motor Speed: " + motorSpeed);
            System.out.println("🔋 Current: " + testMotor.getSupplyCurrent().getValueAsDouble() + "A");
            System.out.println("🌡️  Temp: " + testMotor.getDeviceTemp().getValueAsDouble() + "°C");
            System.out.println("────────────────────────────────────────\n");
        }
    }
    
    @Override
    public void disabledInit() {
        testMotor.setControl(new DutyCycleOut(0.0));
        System.out.println("Disabled - Motor stopped");
    }
}
