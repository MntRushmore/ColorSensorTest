package frc.robot;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Ball Sorting Subsystem
 * Handles intake, color detection, and routing of balls to either shooter or reject
 */
public class BallSortingSubsystem {
    
    // Motors
    private final TalonFX intakeMotor;
    private final TalonFX shooterMotor;
    private final TalonFX rejectMotor;
    
    // Color sensor
    private final ColorSensorV3 colorSensor;
    private final ColorMatch colorMatcher;
    
    // State tracking
    private enum SortingState {
        IDLE,               // No ball detected, waiting
        BALL_DETECTED,      // Ball seen, checking color
        ROUTING_TO_SHOOTER, // Correct color, sending to shooter
        ROUTING_TO_REJECT,  // Wrong color, ejecting
        CLEARING            // Waiting for ball to fully clear
    }
    
    private SortingState currentState = SortingState.IDLE;
    private long stateStartTime = 0;
    private boolean lastBallDetected = false;
    private String lastDetectedColor = "None";
    private boolean intakeEnabled = false;
    
    /**
     * Constructor
     */
    public BallSortingSubsystem(ColorSensorV3 colorSensor) {
        // Initialize motors
        intakeMotor = new TalonFX(Constants.MotorIDs.INTAKE_MOTOR);
        shooterMotor = new TalonFX(Constants.MotorIDs.SHOOTER_MOTOR);
        rejectMotor = new TalonFX(Constants.MotorIDs.REJECT_MOTOR);
        
        // Initialize color sensor and matcher
        this.colorSensor = colorSensor;
        this.colorMatcher = new ColorMatch();
        
        // Add color targets
        colorMatcher.addColorMatch(Constants.Colors.BLUE_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.GREEN_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.RED_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.YELLOW_TARGET);
    }
    
    /**
     * Enable the intake system
     */
    public void enableIntake() {
        intakeEnabled = true;
    }
    
    /**
     * Disable the intake system
     */
    public void disableIntake() {
        intakeEnabled = false;
        stopAllMotors();
        currentState = SortingState.IDLE;
    }
    
    /**
     * Check if intake is enabled
     */
    public boolean isIntakeEnabled() {
        return intakeEnabled;
    }
    
    /**
     * Main periodic function - call this in your robot's periodic method
     */
    public void periodic() {
        if (!intakeEnabled) {
            return;
        }
        
        // Get color sensor data
        Color detectedColor = colorSensor.getColor();
        int proximity = colorSensor.getProximity();
        boolean ballDetected = proximity > Constants.BallSorting.PROXIMITY_THRESHOLD;
        
        // State machine for ball sorting
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
        
        // Update telemetry
        updateTelemetry(detectedColor, proximity, ballDetected);
        lastBallDetected = ballDetected;
    }
    
    /**
     * IDLE state - waiting for ball, intake running
     */
    private void handleIdleState(boolean ballDetected, Color detectedColor) {
        // Run intake motor
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.INTAKE_SPEED));
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        // Check if ball just entered (rising edge detection)
        if (ballDetected && !lastBallDetected) {
            currentState = SortingState.BALL_DETECTED;
            stateStartTime = System.currentTimeMillis();
            
            // Match color
            ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);
            lastDetectedColor = getColorString(match.color);
            
            System.out.println("Ball detected! Color: " + lastDetectedColor + 
                             " | Confidence: " + match.confidence);
        }
    }
    
    /**
     * BALL_DETECTED state - determine routing based on color
     */
    private void handleBallDetectedState() {
        // Stop intake, check color and route accordingly
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        Color detectedColor = colorSensor.getColor();
        ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);
        
        // Check if color matches desired color and confidence is high enough
        if (match.confidence >= Constants.BallSorting.COLOR_CONFIDENCE_THRESHOLD) {
            if (isDesiredColor(match.color)) {
                // Route to shooter
                currentState = SortingState.ROUTING_TO_SHOOTER;
                System.out.println("✓ Correct color! Routing to SHOOTER");
            } else {
                // Route to reject
                currentState = SortingState.ROUTING_TO_REJECT;
                System.out.println("✗ Wrong color! Routing to REJECT");
            }
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    /**
     * ROUTING_TO_SHOOTER state - send ball to shooter
     */
    private void handleRoutingToShooterState() {
        // Run shooter motor forward
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.SHOOTER_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        // Check if duration has elapsed
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.SORT_DURATION_MS) {
            currentState = SortingState.CLEARING;
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    /**
     * ROUTING_TO_REJECT state - eject ball
     */
    private void handleRoutingToRejectState() {
        // Run reject motor forward
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.REJECT_SPEED));
        
        // Check if duration has elapsed
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.SORT_DURATION_MS) {
            currentState = SortingState.CLEARING;
            stateStartTime = System.currentTimeMillis();
        }
    }
    
    /**
     * CLEARING state - wait for ball to fully clear before accepting next ball
     */
    private void handleClearingState(boolean ballDetected) {
        // Stop all sorting motors
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        
        // Check if clearing time has elapsed and ball is gone
        long elapsed = System.currentTimeMillis() - stateStartTime;
        if (elapsed >= Constants.BallSorting.BALL_CLEAR_TIME_MS && !ballDetected) {
            currentState = SortingState.IDLE;
            System.out.println("Ready for next ball!");
        }
    }
    
    /**
     * Stop all motors
     */
    private void stopAllMotors() {
        intakeMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        shooterMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
        rejectMotor.setControl(new DutyCycleOut(Constants.BallSorting.STOP_SPEED));
    }
    
    /**
     * Check if detected color matches the desired color
     */
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
    
    /**
     * Get color name as string
     */
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
    
    /**
     * Update SmartDashboard telemetry
     */
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
    
    /**
     * Get current state (for debugging)
     */
    public String getCurrentState() {
        return currentState.toString();
    }
}
