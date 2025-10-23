package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ColorSensorTestRobot extends TimedRobot {
    
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    private final ColorMatch colorMatcher = new ColorMatch();
    
    private final Joystick controller = new Joystick(0);
    
    private boolean calibrationMode = false;
    private Color calibratedColor = null;
    private String calibratedColorName = "None";
    
    @Override
    public void robotInit() {
        System.out.println("=== COLOR SENSOR TEST MODE ===");
        System.out.println("Sensor will continuously display color data");
        System.out.println("Button 1 (A): Calibrate Red");
        System.out.println("Button 2 (B): Calibrate Blue");
        System.out.println("Button 3 (X): Calibrate Green");
        System.out.println("Button 4 (Y): Calibrate Yellow");
        System.out.println("Button 5 (LB): Save current color as calibration");
        System.out.println("===============================");
        
        colorMatcher.addColorMatch(Constants.Colors.BLUE_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.GREEN_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.RED_TARGET);
        colorMatcher.addColorMatch(Constants.Colors.YELLOW_TARGET);
    }
    
    @Override
    public void teleopPeriodic() {
        Color detectedColor = colorSensor.getColor();
        int proximity = colorSensor.getProximity();
        ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);
        
        String matchedColorName;
        if (match.color.equals(Constants.Colors.BLUE_TARGET)) {
            matchedColorName = "Blue";
        } else if (match.color.equals(Constants.Colors.RED_TARGET)) {
            matchedColorName = "Red";
        } else if (match.color.equals(Constants.Colors.GREEN_TARGET)) {
            matchedColorName = "Green";
        } else if (match.color.equals(Constants.Colors.YELLOW_TARGET)) {
            matchedColorName = "Yellow";
        } else {
            matchedColorName = "Unknown";
        }
        
        if (controller.getRawButtonPressed(1)) {
            System.out.println("Testing against RED target");
            System.out.println("Expected: Red | Actual: " + matchedColorName);
        }
        
        if (controller.getRawButtonPressed(2)) {
            System.out.println("Testing against BLUE target");
            System.out.println("Expected: Blue | Actual: " + matchedColorName);
        }
        
        if (controller.getRawButtonPressed(3)) {
            System.out.println("Testing against GREEN target");
            System.out.println("Expected: Green | Actual: " + matchedColorName);
        }
        
        if (controller.getRawButtonPressed(4)) {
            System.out.println("Testing against YELLOW target");
            System.out.println("Expected: Yellow | Actual: " + matchedColorName);
        }
        
        if (controller.getRawButtonPressed(5)) {
            calibratedColor = detectedColor;
            calibratedColorName = matchedColorName;
            System.out.println("=== CALIBRATION SAVED ===");
            System.out.println("Color: " + calibratedColorName);
            System.out.println("RGB: " + String.format("%.3f, %.3f, %.3f", 
                detectedColor.red, detectedColor.green, detectedColor.blue));
            System.out.println("Proximity: " + proximity);
            System.out.println("Confidence: " + match.confidence);
            System.out.println("========================");
        }
        
        SmartDashboard.putString("Detected Color", matchedColorName);
        SmartDashboard.putNumber("Confidence", match.confidence);
        SmartDashboard.putNumber("Proximity", proximity);
        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);
        SmartDashboard.putBoolean("Ball Detected", proximity > Constants.BallSorting.PROXIMITY_THRESHOLD);
        SmartDashboard.putString("Calibrated Color", calibratedColorName);
        
        if (calibratedColor != null) {
            SmartDashboard.putNumber("Cal Red", calibratedColor.red);
            SmartDashboard.putNumber("Cal Green", calibratedColor.green);
            SmartDashboard.putNumber("Cal Blue", calibratedColor.blue);
        }
        
        if (proximity > Constants.BallSorting.PROXIMITY_THRESHOLD) {
            System.out.printf("BALL DETECTED | Color: %s | Confidence: %.2f | Proximity: %d | RGB: %.3f, %.3f, %.3f%n",
                matchedColorName, match.confidence, proximity,
                detectedColor.red, detectedColor.green, detectedColor.blue);
        }
    }
    
    @Override
    public void disabledInit() {
        System.out.println("Color sensor testing disabled");
    }
}
