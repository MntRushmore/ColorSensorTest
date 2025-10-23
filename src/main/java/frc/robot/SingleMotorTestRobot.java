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
 */
public class SingleMotorTestRobot extends TimedRobot {
    
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
        
        // Initialize the test motor (using intake motor ID by default)
        testMotor = new TalonFX(Constants.MotorIDs.INTAKE_MOTOR);
        
        System.out.println("Motor initialized on CAN ID: " + Constants.MotorIDs.INTAKE_MOTOR);
        
        // Check motor status
        try {
            double temp = testMotor.getDeviceTemp().getValueAsDouble();
            System.out.println("Motor detected! Temperature: " + temp + "C");
            System.out.println("Motor firmware version: " + testMotor.getVersion().getValue());
        } catch (Exception e) {
            System.out.println("⚠️  WARNING: Could not read motor status - check CAN connection!");
            System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("Ready to test!");
    }
    
    @Override
    public void teleopPeriodic() {
        // PS4 Button mapping
        if (controller.getRawButtonPressed(2)) { // Cross (X)
            currentMode = MotorMode.INTAKE;
            System.out.println(">>> INTAKE MODE - Running at " + intakeSpeed);
        }
        
        if (controller.getRawButtonPressed(3)) { // Circle (O)
            currentMode = MotorMode.OUTTAKE_1;
            System.out.println(">>> OUTTAKE 1 MODE - Running at " + outtake1Speed);
        }
        
        if (controller.getRawButtonPressed(1)) { // Square
            currentMode = MotorMode.OUTTAKE_2;
            System.out.println(">>> OUTTAKE 2 MODE - Running at " + outtake2Speed);
        }
        
        if (controller.getRawButtonPressed(4)) { // Triangle
            currentMode = MotorMode.STOPPED;
            System.out.println(">>> STOPPED");
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
        
        // Debug output every 50 cycles (~1 second)
        if (Math.random() < 0.02) {
            System.out.println("[DEBUG] Current Mode: " + currentMode + 
                             " | Motor Speed: " + motorSpeed +
                             " | Motor Temp: " + testMotor.getDeviceTemp().getValueAsDouble() + "C" +
                             " | Motor Current: " + testMotor.getSupplyCurrent().getValueAsDouble() + "A");
        }
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
    
    @Override
    public void robotPeriodic() {
        // This runs in all modes
    }
}
