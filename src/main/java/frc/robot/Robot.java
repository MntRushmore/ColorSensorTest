package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {
    // Color sensor setup
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    
    // Drive motors
    private final TalonFX krakenLeft = new TalonFX(Constants.MotorIDs.DRIVE_LEFT);
    private final TalonFX krakenRight = new TalonFX(Constants.MotorIDs.DRIVE_RIGHT);
    
    // Ball sorting subsystem
    private BallSortingSubsystem ballSortingSubsystem;
    
    // Controller
    private final Joystick controller = new Joystick(Constants.OI.DRIVER_CONTROLLER_PORT);
    
    // Button state tracking for edge detection
    private boolean lastIntakeButtonState = false;
    private boolean lastStopButtonState = false;

    @Override
    public void robotInit() {
        System.out.println("=== Robot Initializing ===");
        
        // Initialize ball sorting subsystem
        ballSortingSubsystem = new BallSortingSubsystem(colorSensor);
        
        System.out.println("Target Color: " + Constants.BallSorting.DESIRED_COLOR);
        System.out.println("=== Robot Ready ===");
    }

    @Override
    public void teleopPeriodic() {
        // ========== DRIVE CONTROL ==========
        double forward = -controller.getRawAxis(Constants.OI.FORWARD_AXIS);
        double turn = controller.getRawAxis(Constants.OI.TURN_AXIS);

        double leftPower = forward + turn;
        double rightPower = forward - turn;

        // Clamp values between -1.0 and 1.0
        leftPower = Math.max(-1.0, Math.min(1.0, leftPower));
        rightPower = Math.max(-1.0, Math.min(1.0, rightPower));

        krakenLeft.setControl(new DutyCycleOut(leftPower));
        krakenRight.setControl(new DutyCycleOut(rightPower));

        // ========== BALL SORTING CONTROL ==========
        // Button to enable intake (A button - button 1)
        boolean intakeButton = controller.getRawButton(Constants.OI.INTAKE_BUTTON);
        if (intakeButton && !lastIntakeButtonState) {
            // Rising edge - button just pressed
            ballSortingSubsystem.enableIntake();
            System.out.println(">>> INTAKE ENABLED <<<");
        }
        lastIntakeButtonState = intakeButton;
        
        // Button to stop intake (B button - button 2)
        boolean stopButton = controller.getRawButton(Constants.OI.STOP_INTAKE_BUTTON);
        if (stopButton && !lastStopButtonState) {
            // Rising edge - button just pressed
            ballSortingSubsystem.disableIntake();
            System.out.println(">>> INTAKE DISABLED <<<");
        }
        lastStopButtonState = stopButton;
        
        // Run ball sorting periodic function
        ballSortingSubsystem.periodic();
    }
    
    @Override
    public void disabledInit() {
        // Stop ball sorting when robot is disabled
        ballSortingSubsystem.disableIntake();
    }
    
    @Override
    public void autonomousInit() {
        // You can enable intake automatically in autonomous if needed
        // ballSortingSubsystem.enableIntake();
    }
    
    @Override
    public void autonomousPeriodic() {
        // Run ball sorting in autonomous
        ballSortingSubsystem.periodic();
    }
}
