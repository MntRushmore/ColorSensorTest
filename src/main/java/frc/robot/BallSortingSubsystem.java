package frc.robot;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallSortingSubsystem {
    
    private final TalonFX intakeMotor;
    private final TalonFX shooterMotor;
    private final TalonFX rejectMotor;
    
    private final ColorSensorV3 colorSensor;
    private final ColorMatch colorMatcher;
    
    private enum SortingState {
        IDLE,
        BALL_DETECTED,
        ROUTING_TO_SHOOTER,
        ROUTING_TO_REJECT,
        CLEARING
    }
    
    private SortingState currentState = SortingState.IDLE;
    private long stateStartTime = 0;
    private boolean lastBallDetected = false;
    private String lastDetectedColor = "None";
    private boolean intakeEnabled = false;
    
    public BallSortingSubsystem(ColorSensorV3 colorSensor) {
        intakeMotor = new TalonFX(Constants.MotorIDs.INTAKE_MOTOR);
        shooterMotor = new TalonFX(Constants.MotorIDs.SHOOTER_MOTOR);
        rejectMotor = new TalonFX(Constants.MotorIDs.REJECT_MOTOR);
        
        this.colorSensor = colorSensor;
        this.colorMatcher = new ColorMatch();
        
        colorMatcher.addColorMatch(Constants.Colors.BLUE_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.GREEN_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.RED_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.YELLOW_TARGET);
    }
    
    public void enableIntake() {
        intakeEnabled = true;
    }
    
    public void disableIntake() {
        intakeEnabled = false;
        stopAllMotors();
        currentState = SortingState.IDLE;
    }
    
    public boolean isIntakeEnabled() {
        return intakeEnabled;
    }
    
    public void periodic() {
        if (!intakeEnabled) {
            return;
        }
        
        Color detectedColor = colorSensor.getColor();
        int proximity = colorSensor.getProximity();
        boolean ballDetected = proximity > Constants.BallSorting.PROXIMITY_THRESHOLD;
        
        switch (currentState) {
            case IDLE:
                handleIdleState(ballDetected, detectedColor);
                break;
                
            case BALL_DETECTED:
                handleBallDetectedState();
                break;
                
            case ROUTING_TO_SHOOTER:
                handleRoutingToShooterState();
                break;
                
            case ROUTING_TO_REJECT:
                handleRoutingToRejectState();
                break;
                
            case CLEARING:
                handleClearingState(ballDetected);
                break;
        }
        
        updateTelemetry(detectedColor, proximity, ballDetected);
        lastBallDetected = ballDetected;
    }
    
    private void handleIdleState(boolean ballDetected, Color detectedColor) {
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.INTAKE_SPEED));
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        if (ballDetected && !lastBallDetected) {
            currentState = SortingState.BALL_DETECTED;
            stateStartTime = System.currentTimeMillis();
            
            ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);
            lastDetectedColor = getColorString(match.color);
            
            System.out.println("Ball detected! Color: " + lastDetectedColor + 
                             " | Confidence: " + match.confidence);
        }
    }
    
    private void handleBallDetectedState() {
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        Color detectedColor = colorSensor.getColor();
        ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);
        
        if (match.confidence >= Constants.BallSorting.COLOR_CONFIDENCE_THRESHOLD) {
            if (isDesiredColor(match.color)) {
                currentState = SortingState.ROUTING_TO_SHOOTER;
                System.out.println("✓ Correct color! Routing to SHOOTER");
            } else {
                currentState = SortingState.ROUTING_TO_REJECT;
                System.out.println("✗ Wrong color! Routing to REJECT");
            }
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    private void handleRoutingToShooterState() {
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.SHOOTER_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.SORT_DURATION_MS) {
            currentState = SortingState.CLEARING;
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    private void handleRoutingToRejectState() {
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.REJECT_SPEED));
        
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.SORT_DURATION_MS) {
            currentState = SortingState.CLEARING;
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    private void handleClearingState(boolean ballDetected) {
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.BALL_CLEAR_TIME_MS && !ballDetected) {
            currentState = SortingState.IDLE;
            System.out.println("Ready for next ball!");
        }
    }
    
    private void stopAllMotors() {
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
    }
    
    private boolean isDesiredColor(Color detectedColor) {
        Constants.BallSorting.TargetColor desiredColor = Constants.BallSorting.DESIRED_COLOR;
        
        if (desiredColor == Constants.BallSorting.TargetColor.BLUE) {
            return detectedColor.equals(Constants.Colors.BLUE_TARGET);
        } else if (desiredColor == Constants.BallSorting.TargetColor.RED) {
            return detectedColor.equals(Constants.Colors.RED_TARGET);
        } else if (desiredColor == Constants.BallSorting.TargetColor.GREEN) {
            return detectedColor.equals(Constants.Colors.GREEN_TARGET);
        } else if (desiredColor == Constants.BallSorting.TargetColor.YELLOW) {
            return detectedColor.equals(Constants.Colors.YELLOW_TARGET);
        }
        
        return false;
    }
    
    private String getColorString(Color color) {
        if (color.equals(Constants.Colors.BLUE_TARGET)) {
            return "Blue";
        } else if (color.equals(Constants.Colors.RED_TARGET)) {
            return "Red";
        } else if (color.equals(Constants.Colors.GREEN_TARGET)) {
            return "Green";
        } else if (color.equals(Constants.Colors.YELLOW_TARGET)) {
            return "Yellow";
        } else {
            return "Unknown";
        }
    }
    
    private void updateTelemetry(Color detectedColor, int proximity, boolean ballDetected) {
        SmartDashboard.putString("Sorting State", currentState.toString());
        SmartDashboard.putBoolean("Ball Detected", ballDetected);
        SmartDashboard.putString("Last Color", lastDetectedColor);
        SmartDashboard.putNumber("Proximity", proximity);
        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);
        SmartDashboard.putBoolean("Intake Enabled", intakeEnabled);
        SmartDashboard.putString("Target Color", Constants.BallSorting.DESIRED_COLOR.toString());
    }
    
    public String getCurrentState() {
        return currentState.toString();
    }
}
