package frc.robot;

import edu.wpi.first.wpilibj.util.Color;

/**
 * Constants file for the robot configuration
 */
public final class Constants {
    
    // Motor CAN IDs
    public static final class MotorIDs {
        public static final int DRIVE_LEFT = 1;
        public static final int DRIVE_RIGHT = 2;
        public static final int INTAKE_MOTOR = 3;
        public static final int SHOOTER_MOTOR = 4;
        public static final int REJECT_MOTOR = 5;
    }
    
    // Ball Sorting System Constants
    public static final class BallSorting {
        // Motor speeds (adjust these based on testing)
        public static final double INTAKE_SPEED = 0.7;
        public static final double SHOOTER_SPEED = 0.8;
        public static final double REJECT_SPEED = 0.75;
        public static final double STOP_SPEED = 0.0;
        
        // Time delays (in milliseconds)
        public static final long SORT_DURATION_MS = 500;  // How long to run shooter/reject motor
        public static final long BALL_CLEAR_TIME_MS = 300; // Time to ensure ball has cleared
        
        // Proximity threshold - ball is detected when proximity exceeds this value
        public static final int PROXIMITY_THRESHOLD = 100;
        
        // Color detection confidence threshold (0.0 to 1.0)
        public static final double COLOR_CONFIDENCE_THRESHOLD = 0.8;
        
        // Target color selection - Change this to your desired ball color
        public static enum TargetColor {
            RED,
            BLUE,
            GREEN,
            YELLOW
        }
        
        // SET YOUR DESIRED COLOR HERE
        public static final TargetColor DESIRED_COLOR = TargetColor.BLUE;
    }
    
    // Color targets from REV Color Sensor V3
    public static final class Colors {
        public static final Color BLUE_TARGET = new Color(0.143, 0.427, 0.429);
        public static final Color GREEN_TARGET = new Color(0.197, 0.561, 0.240);
        public static final Color RED_TARGET = new Color(0.561, 0.232, 0.114);
        public static final Color YELLOW_TARGET = new Color(0.361, 0.524, 0.113);
    }
    
    // Controller configuration
    public static final class OI {
        public static final int DRIVER_CONTROLLER_PORT = 0;
        public static final int FORWARD_AXIS = 1;
        public static final int TURN_AXIS = 0;
        public static final int INTAKE_BUTTON = 1;  // A button
        public static final int STOP_INTAKE_BUTTON = 2;  // B button
    }
}
