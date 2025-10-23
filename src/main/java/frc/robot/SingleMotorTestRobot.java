package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

/**
 * Single Motor Test Robot
 * 
 * This test robot is designed for testing with only ONE physical motor.
 * Different buttons will run the motor at different speeds to simulate:
 * - Fast intake speed
 * - Two different outtake speeds
 * 
 * This helps test your controller mapping before you have all motors installed.
 * 
 * MOTOR CAN ID CONFIGURATION:
 * - Change MOTOR_CAN_ID below to match your motor (0 or 1)
 */
public class SingleMotorTestRobot extends TimedRobot {
    
    // *** CONFIGURE THIS: Set to your motor's CAN ID (0 or 1) ***
    private static final int MOTOR_CAN_ID = 0;  // Change to 1 if testing the other motor
    
    // Single motor for testing
    private TalonFX testMotor;
    
    private final Joystick controller = new Joystick(0);
    
    // Different speed modes
    private enum MotorMode {
        STOPPED,
        INTAKE,      // Fast intake speed
        OUTTAKE_1,   // First outtake speed (shooter)
        OUTTAKE_2    // Second outtake speed (reject)
    }
    
    private MotorMode currentMode = MotorMode.STOPPED;
    
    // Speed settings (can be adjusted with D-Pad)
    private double intakeSpeed = 0.7;   // Fast intake
    private double outtake1Speed = 0.8;  // Shooter outtake
    private double outtake2Speed = 0.75; // Reject outtake
    
    @Override
    public void robotInit() {
        System.out.println("========================================");
        System.out.println("=== SINGLE MOTOR TEST ROBOT MODE ===");
        System.out.println("========================================");
        System.out.println("PS4 Controller Button Mapping:");
        System.out.println("  Cross (X) - Button 2: INTAKE (Fast Speed: " + intakeSpeed + ")");
        System.out.println("  Circle (O) - Button 3: OUTTAKE 1 (Speed: " + outtake1Speed + ")");
        System.out.println("  Square - Button 1: OUTTAKE 2 (Speed: " + outtake2Speed + ")");
        System.out.println("  Triangle - Button 4: STOP MOTOR");
        System.out.println("");
        System.out.println("Speed Adjustment:");
        System.out.println("  D-Pad Up: Increase current speed by 0.05");
        System.out.println("  D-Pad Down: Decrease current speed by 0.05");
        System.out.println("");
        System.out.println("Notes:");
        System.out.println("  - Only ONE physical motor needed");
        System.out.println("  - Each button runs motor at different speed");
        System.out.println("  - Using PS4 controller mapping");
        System.out.println("========================================");
        
        // Initialize the test motor with correct CAN ID
        testMotor = new TalonFX(MOTOR_CAN_ID);
        
        System.out.println("Motor initialized on CAN ID: " + MOTOR_CAN_ID);
        
        // Check motor status
        try {
            double temp = testMotor.getDeviceTemp().getValueAsDouble();
            System.out.println("✅ Motor detected! Temperature: " + temp + "°C");
            System.out.println("Firmware version: " + testMotor.getVersion().getValue());
        } catch (Exception e) {
            System.out.println("⚠️  WARNING: Could not read motor status!");
            System.out.println("Check: 1) Motor is powered, 2) CAN ID is correct, 3) CAN wiring");
            System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("");
        System.out.println("⚠️  MOTOR STARTS IN STOPPED MODE");
        System.out.println("Press any button to start the motor!");
        System.out.println("Ready to test!");
    }
    
    @Override
    public void teleopPeriodic() {
        // PS4 Button mapping
        if (controller.getRawButtonPressed(2)) { // Cross (X)
            currentMode = MotorMode.INTAKE;
            System.out.println(">>> ✅ INTAKE MODE ACTIVATED - Running at " + intakeSpeed);
        }
        
        if (controller.getRawButtonPressed(3)) { // Circle (O)
            currentMode = MotorMode.OUTTAKE_1;
            System.out.println(">>> ✅ OUTTAKE 1 MODE ACTIVATED - Running at " + outtake1Speed);
        }
        
        if (controller.getRawButtonPressed(1)) { // Square
            currentMode = MotorMode.OUTTAKE_2;
            System.out.println(">>> ✅ OUTTAKE 2 MODE ACTIVATED - Running at " + outtake2Speed);
        }
        
        if (controller.getRawButtonPressed(4)) { // Triangle
            currentMode = MotorMode.STOPPED;
            System.out.println(">>> ⛔ MOTOR STOPPED");
        }
        
        // Speed adjustment with D-Pad
        int dPad = controller.getPOV();
        if (dPad == 0) { // D-Pad Up
            adjustSpeed(0.05);
        } else if (dPad == 180) { // D-Pad Down
            adjustSpeed(-0.05);
        }
        
        // Run motor based on current mode
        double motorSpeed = 0.0;
        switch (currentMode) {
            case INTAKE:
                motorSpeed = intakeSpeed;
                break;
            case OUTTAKE_1:
                motorSpeed = outtake1Speed;
                break;
            case OUTTAKE_2:
                motorSpeed = outtake2Speed;
                break;
            case STOPPED:
                motorSpeed = 0.0;
                break;
        }
        
        testMotor.setControl(new DutyCycleOut(motorSpeed));
    }
    
    /**
     * Adjust the speed of the currently active mode
     */
    private void adjustSpeed(double delta) {
        switch (currentMode) {
            case INTAKE:
                intakeSpeed = clampSpeed(intakeSpeed + delta);
                System.out.println("Intake speed adjusted to: " + intakeSpeed);
                break;
            case OUTTAKE_1:
                outtake1Speed = clampSpeed(outtake1Speed + delta);
                System.out.println("Outtake 1 speed adjusted to: " + outtake1Speed);
                break;
            case OUTTAKE_2:
                outtake2Speed = clampSpeed(outtake2Speed + delta);
                System.out.println("Outtake 2 speed adjusted to: " + outtake2Speed);
                break;
            case STOPPED:
                System.out.println("Cannot adjust speed when stopped. Press a button to select a mode.");
                break;
        }
    }
    
    /**
     * Clamp speed between 0.0 and 1.0
     */
    private double clampSpeed(double speed) {
        return Math.max(0.0, Math.min(1.0, speed));
    }
    
    @Override
    public void disabledInit() {
        testMotor.setControl(new DutyCycleOut(0.0));
        System.out.println("Motor testing disabled - Motor stopped");
    }
    
    private int loopCounter = 0;
    
    @Override
    public void robotPeriodic() {
        // Print status every 50 loops (~1 second)
        loopCounter++;
        if (loopCounter >= 50) {
            loopCounter = 0;
            
            double speed = 0.0;
            switch (currentMode) {
                case INTAKE: speed = intakeSpeed; break;
                case OUTTAKE_1: speed = outtake1Speed; break;
                case OUTTAKE_2: speed = outtake2Speed; break;
                case STOPPED: speed = 0.0; break;
            }
            
            System.out.println("────────────────────────────────────────");
            System.out.println("📊 STATUS: Mode=" + currentMode + " | Speed=" + speed);
            System.out.println("🌡️  Temp: " + testMotor.getDeviceTemp().getValueAsDouble() + "°C");
            System.out.println("⚡ Current: " + testMotor.getSupplyCurrent().getValueAsDouble() + "A");
            System.out.println("────────────────────────────────────────");
        }
    }
}
