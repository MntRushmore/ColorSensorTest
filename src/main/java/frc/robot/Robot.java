package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.DutyCycleOut;

public class Robot extends TimedRobot {
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    private final ColorMatch colorMatcher = new ColorMatch();
    private final Color kBlueTarget = new Color(0.143, 0.427, 0.429);
    private final Color kGreenTarget = new Color(0.197, 0.561, 0.240);
    private final Color kRedTarget = new Color(0.561, 0.232, 0.114);
    private final Color kYellowTarget = new Color(0.361, 0.524, 0.113);


    private final TalonFX krakenLeft = new TalonFX(1);
    private final TalonFX krakenRight = new TalonFX(2);


    private final Joystick controller = new Joystick(0);

    @Override
    public void robotInit() {
        colorMatcher.addColorMatch(kBlueTarget);
        colorMatcher.addColorMatch(kGreenTarget);
        colorMatcher.addColorMatch(kRedTarget);
        colorMatcher.addColorMatch(kYellowTarget);
    }

    @Override
    public void teleopPeriodic() {
        double forward = -controller.getRawAxis(1);
        double turn = controller.getRawAxis(0);

        double leftPower = forward + turn;
        double rightPower = forward - turn;

        leftPower = Math.max(-1.0, Math.min(1.0, leftPower));
        rightPower = Math.max(-1.0, Math.min(1.0, rightPower));

        krakenLeft.setControl(new DutyCycleOut(leftPower));
        krakenRight.setControl(new DutyCycleOut(rightPower));

        Color detectedColor = colorSensor.getColor();
        int proximity = colorSensor.getProximity();
        ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

        String colorString;
        if (match.color == kBlueTarget) {
            colorString = "Blue";
        } else if (match.color == kRedTarget) {
            colorString = "Red";
        } else if (match.color == kGreenTarget) {
            colorString = "Green";
        } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
        } else {
            colorString = "Unknown";
        }

        System.out.printf(
            "Color: %s | R: %.2f G: %.2f B: %.2f | Proximity: %d | Left: %.2f Right: %.2f%n",
            colorString, detectedColor.red, detectedColor.green, detectedColor.blue, proximity, leftPower, rightPower);
    }
}
