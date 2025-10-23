package frc.robot;

import edu.wpi.first.wpilibj.util.Color;

public final class Constants {
    
    public static final class MotorIDs {
        public static final int DRIVE_LEFT = 1;
        public static final int DRIVE_RIGHT = 2;
        public static final int INTAKE_MOTOR = 3;
        public static final int SHOOTER_MOTOR = 4;
        public static final int REJECT_MOTOR = 5;
    }
    
    public static final class BallSorting {
        public static final double INTAKE_SPEED = 0.7;
        public static final double SHOOTER_SPEED = 0.8;
        public static final double REJECT_SPEED = 0.75;
        public static final double STOP_SPEED = 0.0;
        
        public static final long SORT_DURATION_MS = 500;
        public static final long BALL_CLEAR_TIME_MS = 300;
        
        public static final int PROXIMITY_THRESHOLD = 100;
        
        public static final double COLOR_CONFIDENCE_THRESHOLD = 0.8;
        
        public static enum TargetColor {
            RED,
            BLUE,
            GREEN,
            YELLOW
        }
        
        public static final TargetColor DESIRED_COLOR = TargetColor.BLUE;
    }
    
    public static final class Colors {
        public static final Color BLUE_TARGET = new Color(0.143, 0.427, 0.429);
        public static final Color GREEN_TARGET = new Color(0.197, 0.561, 0.240);
        public static final Color RED_TARGET = new Color(0.561, 0.232, 0.114);
        public static final Color YELLOW_TARGET = new Color(0.361, 0.524, 0.113);
    }
    
    public static final class OI {
        public static final int DRIVER_CONTROLLER_PORT = 0;
        public static final int FORWARD_AXIS = 1;
        public static final int TURN_AXIS = 0;
        public static final int INTAKE_BUTTON = 1;
        public static final int STOP_INTAKE_BUTTON = 2;
    }
}
