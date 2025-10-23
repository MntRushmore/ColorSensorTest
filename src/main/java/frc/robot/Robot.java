package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    
    private final TalonFX krakenLeft = new TalonFX(Constants.MotorIDs.DRIVE_LEFT);
    private final TalonFX krakenRight = new TalonFX(Constants.MotorIDs.DRIVE_RIGHT);
    
    private BallSortingSubsystem ballSortingSubsystem;
    
    private final Joystick controller = new Joystick(Constants.OI.DRIVER_CONTROLLER_PORT);
    
    private boolean lastIntakeButtonState = false;
    private boolean lastStopButtonState = false;

    @Override
    public void robotInit() {
        System.out.println("=== Robot Initializing ===");
        
        ballSortingSubsystem = new BallSortingSubsystem(colorSensor);
        
        System.out.println("Target Color: " + Constants.BallSorting.DESIRED_COLOR);
        System.out.println("=== Robot Ready ===");
    }

    @Override
    public void teleopPeriodic() {
        double forward = -controller.getRawAxis(Constants.OI.FORWARD_AXIS);
        double turn = controller.getRawAxis(Constants.OI.TURN_AXIS);

        double leftPower = forward + turn;
        double rightPower = forward - turn;

        leftPower = Math.max(-1.0, Math.min(1.0, leftPower));
        rightPower = Math.max(-1.0, Math.min(1.0, rightPower));

        krakenLeft.setControl(new DutyCycleOut(leftPower));
        krakenRight.setControl(new DutyCycleOut(rightPower));

        boolean intakeButton = controller.getRawButton(Constants.OI.INTAKE_BUTTON);
        if (intakeButton && !lastIntakeButtonState) {
            ballSortingSubsystem.enableIntake();
            System.out.println(">>> INTAKE ENABLED <<<");
        }
        lastIntakeButtonState = intakeButton;
        
        boolean stopButton = controller.getRawButton(Constants.OI.STOP_INTAKE_BUTTON);
        if (stopButton && !lastStopButtonState) {
            ballSortingSubsystem.disableIntake();
            System.out.println(">>> INTAKE DISABLED <<<");
        }
        lastStopButtonState = stopButton;
        
        ballSortingSubsystem.periodic();
    }
    
    @Override
    public void disabledInit() {
        ballSortingSubsystem.disableIntake();
    }
    
    @Override
    public void autonomousInit() {
    }
    
    @Override
    public void autonomousPeriodic() {
        ballSortingSubsystem.periodic();
    }
}
