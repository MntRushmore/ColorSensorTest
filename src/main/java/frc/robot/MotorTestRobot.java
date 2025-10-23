package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotorTestRobot extends TimedRobot {
    
    private TalonFX driveLeft;
    private TalonFX driveRight;
    private TalonFX intakeMotor;
    private TalonFX shooterMotor;
    private TalonFX rejectMotor;
    
    private final Joystick controller = new Joystick(0);
    
    private enum TestMode {
        DRIVE_LEFT,
        DRIVE_RIGHT,
        INTAKE,
        SHOOTER,
        REJECT,
        ALL_STOP
    }
    
    private TestMode currentMode = TestMode.ALL_STOP;
    private double testSpeed = 0.3;
    
    @Override
    public void robotInit() {
        System.out.println("=== MOTOR TEST MODE ===");
        System.out.println("Button 1 (A): Test Drive Left");
        System.out.println("Button 2 (B): Test Drive Right");
        System.out.println("Button 3 (X): Test Intake Motor");
        System.out.println("Button 4 (Y): Test Shooter Motor");
        System.out.println("Button 5 (LB): Test Reject Motor");
        System.out.println("Button 6 (RB): STOP ALL");
        System.out.println("D-Pad Up: Increase Speed");
        System.out.println("D-Pad Down: Decrease Speed");
        System.out.println("=========================");
        
        driveLeft = new TalonFX(Constants.MotorIDs.DRIVE_LEFT);
        driveRight = new TalonFX(Constants.MotorIDs.DRIVE_RIGHT);
        intakeMotor = new TalonFX(Constants.MotorIDs.INTAKE_MOTOR);
        shooterMotor = new TalonFX(Constants.MotorIDs.SHOOTER_MOTOR);
        rejectMotor = new TalonFX(Constants.MotorIDs.REJECT_MOTOR);
    }
    
    @Override
    public void teleopPeriodic() {
        if (controller.getRawButtonPressed(1)) {
            currentMode = TestMode.DRIVE_LEFT;
            System.out.println("Testing DRIVE LEFT at speed: " + testSpeed);
        }
        
        if (controller.getRawButtonPressed(2)) {
            currentMode = TestMode.DRIVE_RIGHT;
            System.out.println("Testing DRIVE RIGHT at speed: " + testSpeed);
        }
        
        if (controller.getRawButtonPressed(3)) {
            currentMode = TestMode.INTAKE;
            System.out.println("Testing INTAKE MOTOR at speed: " + testSpeed);
        }
        
        if (controller.getRawButtonPressed(4)) {
            currentMode = TestMode.SHOOTER;
            System.out.println("Testing SHOOTER MOTOR at speed: " + testSpeed);
        }
        
        if (controller.getRawButtonPressed(5)) {
            currentMode = TestMode.REJECT;
            System.out.println("Testing REJECT MOTOR at speed: " + testSpeed);
        }
        
        if (controller.getRawButtonPressed(6)) {
            currentMode = TestMode.ALL_STOP;
            System.out.println("ALL MOTORS STOPPED");
        }
        
        int dPad = controller.getPOV();
        if (dPad == 0) {
            testSpeed = Math.min(1.0, testSpeed + 0.05);
            System.out.println("Speed increased to: " + testSpeed);
        } else if (dPad == 180) {
            testSpeed = Math.max(0.0, testSpeed - 0.05);
            System.out.println("Speed decreased to: " + testSpeed);
        }
        
        stopAllMotors();
        
        switch (currentMode) {
            case DRIVE_LEFT:
                driveLeft.setControl(new DutyCycleOut(testSpeed));
                break;
            case DRIVE_RIGHT:
                driveRight.setControl(new DutyCycleOut(testSpeed));
                break;
            case INTAKE:
                intakeMotor.setControl(new DutyCycleOut(testSpeed));
                break;
            case SHOOTER:
                shooterMotor.setControl(new DutyCycleOut(testSpeed));
                break;
            case REJECT:
                rejectMotor.setControl(new DutyCycleOut(testSpeed));
                break;
            case ALL_STOP:
                break;
        }
        
        SmartDashboard.putString("Test Mode", currentMode.toString());
        SmartDashboard.putNumber("Test Speed", testSpeed);
        SmartDashboard.putNumber("Drive Left Current", driveLeft.getSupplyCurrent().getValue());
        SmartDashboard.putNumber("Drive Right Current", driveRight.getSupplyCurrent().getValue());
        SmartDashboard.putNumber("Intake Current", intakeMotor.getSupplyCurrent().getValue());
        SmartDashboard.putNumber("Shooter Current", shooterMotor.getSupplyCurrent().getValue());
        SmartDashboard.putNumber("Reject Current", rejectMotor.getSupplyCurrent().getValue());
    }
    
    @Override
    public void disabledInit() {
        stopAllMotors();
        System.out.println("Motor testing disabled");
    }
    
    private void stopAllMotors() {
        driveLeft.setControl(new DutyCycleOut(0.0));
        driveRight.setControl(new DutyCycleOut(0.0));
        intakeMotor.setControl(new DutyCycleOut(0.0));
        shooterMotor.setControl(new DutyCycleOut(0.0));
        rejectMotor.setControl(new DutyCycleOut(0.0));
    }
}
