# FRC Dashboard - Quick Start Guide 🚀

## ✅ Fixed! The dashboard module is now properly configured.

## 🎯 How to Run the Dashboard

### Option 1: Run Directly (Recommended for Development)

```bash
# From your project root directory
./gradlew :dashboard:run
```

This will:
- Build the dashboard automatically
- Launch the JavaFX application
- Open the purple-themed GUI window

### Option 2: Build and Run JAR (Recommended for Distribution)

```bash
# Build the standalone JAR
./gradlew :dashboard:jar

# Run the JAR
java -jar dashboard/build/libs/frc-dashboard-1.0.0.jar
```

### Option 3: Build Everything

```bash
# Build both robot code and dashboard
./gradlew build

# Build only dashboard
./gradlew :dashboard:build
```

## 🔗 Connecting to Your Robot

### For Testing/Simulation (No Robot Required)

1. **Start the dashboard**: `./gradlew :dashboard:run`
2. **Default connection**: Already set to `localhost`
3. **Click**: "CONNECT TO ROBOT" button
4. **Note**: You won't see real data without a robot or simulation running

### For Real Robot Connection

1. **Start your robot** and ensure it's on the network
2. **Find your robot IP**:
   - Competition: `10.TE.AM.2` (replace TE.AM with your team number)
   - Example for team 254: `10.2.54.2`
   - USB Connection: `172.22.11.2`
   - mDNS: `roborio-TEAM-frc.local`
3. **Start the dashboard**: `./gradlew :dashboard:run`
4. **Enter robot IP** in the connection field
5. **Click**: "CONNECT TO ROBOT"
6. **Watch status**: Indicator turns green when connected ✅

### For Robot Simulation

```bash
# Terminal 1 - Start robot simulation
./gradlew simulateJava

# Terminal 2 - Start dashboard
./gradlew :dashboard:run
```

The dashboard will automatically connect to `localhost` and show simulated data.

## 🎮 Using the Dashboard

### 1. Color Sensor Monitoring

**What you'll see:**
- **Proximity**: Distance value (0-255+)
- **RGB Values**: Red, Green, Blue components (0.0-1.0)
- **Confidence**: How sure the sensor is about the color (0.0-1.0)
- **Detected Color**: Name of detected color (Red/Blue/Green/Yellow)
- **Color Preview Box**: Visual representation of the RGB values

**How to use:**
1. Place a ball near the color sensor
2. Watch the proximity value increase
3. See the RGB values change in real-time
4. View the detected color name
5. Check the color preview box to see the actual color

### 2. Robot State Monitoring

**What you'll see:**
- **Sorting State**: Current state (IDLE, BALL_DETECTED, ROUTING_TO_SHOOTER, etc.)
- **Ball Detected**: ✓ YES or ✗ NO
- **Last Color**: Most recently processed ball
- **Target Color**: What color you're sorting for
- **Intake Enabled**: Whether intake is running

**Color coding:**
- **IDLE**: Light purple
- **BALL_DETECTED**: Gold
- **ROUTING_TO_SHOOTER**: Green (correct color)
- **ROUTING_TO_REJECT**: Red (wrong color)
- **CLEARING**: Orange

### 3. Manual Motor Testing

**Important**: Make sure robot is in **TEST MODE** before using these controls!

**Steps:**
1. **Adjust speed slider** (0.0 to 1.0)
   - 0.0 = stopped
   - 0.5 = half speed
   - 1.0 = full speed
2. **Click test button** for the motor you want to test:
   - `TEST INTAKE` - Runs intake motor
   - `TEST SHOOTER` - Runs shooter motor  
   - `TEST REJECT` - Runs reject motor
3. **Emergency stop**: Click `⏹ STOP ALL` to stop everything

**Safety tips:**
- Start with low speeds (0.3-0.5)
- Have your hand on the STOP ALL button
- Make sure robot is secured and safe to run
- Test one motor at a time

### 4. Color Calibration

**When to calibrate:**
- First time setup
- Different lighting conditions
- Competition field vs practice field
- Colors not detecting correctly

**Calibration steps:**

1. **Prepare your sample ball**:
   - Use the actual game pieces from competition
   - Make sure the ball is clean

2. **For each color (Red, Blue, Green, Yellow)**:
   - Place ball close to sensor (proximity > 100)
   - Wait for RGB values to stabilize (stop changing)
   - Click the corresponding calibration button:
     - `🔴 CALIBRATE RED`
     - `🔵 CALIBRATE BLUE`
     - `🟢 CALIBRATE GREEN`
     - `🟡 CALIBRATE YELLOW`
   - Robot will save the current RGB values as reference

3. **Save calibration**:
   - After calibrating all needed colors
   - Click `💾 SAVE CALIBRATION`
   - Settings will be saved to robot

4. **Test your calibration**:
   - Try each color ball again
   - Check that "Detected Color" is correct
   - Check that "Confidence" is high (> 0.8)
   - If not working well, adjust thresholds (see below)

### 5. Configuration Settings

#### Set Target Color
**What it does**: Tells the robot which color balls to keep (send to shooter) vs reject

**Steps:**
1. Select color from dropdown (RED, BLUE, GREEN, or YELLOW)
2. Click `SET TARGET`
3. Robot will now route matching balls to shooter, others to reject

#### Adjust Proximity Threshold
**What it does**: Changes how close a ball must be to be detected

**How to adjust:**
- **Slider range**: 50 to 200
- **Lower values** (50-80): Detect balls farther away (more sensitive)
- **Higher values** (120-200): Require ball to be closer (less false positives)
- **Default**: 100 (recommended starting point)

**When to adjust:**
- False detections: Increase threshold
- Missing balls: Decrease threshold
- Different sensor mounting: Adjust as needed

#### Adjust Confidence Threshold
**What it does**: Sets minimum confidence required for color matching

**How to adjust:**
- **Slider range**: 0.5 to 1.0
- **Lower values** (0.5-0.7): More lenient matching (may accept wrong colors)
- **Higher values** (0.85-1.0): Stricter matching (may reject correct colors)
- **Default**: 0.8 (recommended)

**When to adjust:**
- Wrong colors getting through: Increase threshold
- Correct colors being rejected: Decrease threshold
- Variable lighting: Lower threshold

## 🐛 Troubleshooting

### Dashboard Won't Start

**Error**: `Cannot locate tasks that match ':dashboard:run'`

**Solution**: 
```bash
# Make sure you're in the project root directory
cd /path/to/your/project

# Verify dashboard module is included
./gradlew projects

# You should see: \--- Project ':dashboard'
```

### Can't Connect to Robot

**Symptoms**: Status stays "● DISCONNECTED" (red)

**Solutions**:
1. **Check robot is powered on and connected to network**
2. **Verify IP address**:
   ```bash
   # Try pinging the robot
   ping 10.TE.AM.2  # Replace with your team number
   ```
3. **Check NetworkTables port** (should be open):
   - NT4: Port 5810
   - NT3: Port 1735
4. **Try different connection methods**:
   - IP address: `10.2.54.2` (for team 254)
   - mDNS: `roborio-2540-frc.local`
   - USB: `172.22.11.2`

### No Data Showing

**Symptoms**: Connected but all values show 0 or "None"

**Solutions**:
1. **Verify robot code is running**:
   - Check Driver Station shows "Robot Code" green
2. **Check robot code publishes to SmartDashboard**:
   - Your `BallSortingSubsystem` should already do this
   - Look for lines like: `SmartDashboard.putNumber("Proximity", ...)`
3. **Use official NetworkTables tool** to verify data is being published:
   - Open Outline Viewer (comes with WPILib)
   - Connect to robot
   - Look for SmartDashboard table entries

### Colors Not Detecting Correctly

**Symptoms**: Wrong colors detected, or low confidence

**Solutions**:
1. **Recalibrate under actual lighting conditions**:
   - Competition field lighting is different from practice
   - Do calibration on the field before matches
2. **Adjust confidence threshold**:
   - Lower it to 0.7 if colors are too strict
3. **Check sensor positioning**:
   - Make sure sensor has clear view of ball
   - Not too close (saturation) or too far
4. **Clean the sensor**:
   - Dust/dirt on sensor affects readings

### Dashboard is Slow/Laggy

**Solutions**:
1. **Close other applications** using Java/Gradle
2. **Increase Java heap size**:
   ```bash
   java -Xmx1024m -jar dashboard/build/libs/frc-dashboard-1.0.0.jar
   ```
3. **Check network connection** to robot:
   - Poor WiFi can cause lag in data updates
   - Try wired connection (USB or Ethernet)

## 📊 Understanding the Data

### Proximity Values
- **0-50**: No ball detected (too far)
- **50-100**: Ball approaching (borderline detection)
- **100-200**: Ball detected (good range)
- **200+**: Ball very close (may be too close)

### Confidence Values
- **0.0-0.5**: Very uncertain (wrong color or no ball)
- **0.5-0.7**: Low confidence (might be right color)
- **0.7-0.85**: Medium confidence (probably right)
- **0.85-1.0**: High confidence (definitely correct color)

### RGB Values
- **Each value**: 0.0 (none) to 1.0 (maximum)
- **Red ball**: High red (0.5-0.6), low green/blue (0.1-0.2)
- **Blue ball**: High blue (0.4-0.5), low red (0.1-0.2), medium green (0.4)
- **Green ball**: High green (0.5-0.6), low red/blue (0.2)
- **Yellow ball**: High red (0.3-0.4), high green (0.5), low blue (0.1)

## 🎨 Customization

### Change Theme Colors

Edit `dashboard/src/main/resources/dashboard.css`:

```css
/* Change primary purple */
.header {
    -fx-background-color: linear-gradient(to bottom, #YOUR_COLOR, #YOUR_COLOR2);
}

/* Change panel backgrounds */
.telemetry-panel {
    -fx-background-color: #YOUR_BACKGROUND_COLOR;
}
```

Then rebuild:
```bash
./gradlew :dashboard:build
```

### Add Custom Telemetry

See `dashboard/README.md` section "Adding New Telemetry Fields" for detailed instructions.

## 🆘 Need More Help?

1. **Check the full documentation**: `dashboard/README.md`
2. **Check visual layout guide**: `DASHBOARD_LAYOUT.md`
3. **Check robot code documentation**: `BALL_SORTING_README.md`
4. **Check testing guide**: `TESTING_GUIDE.md`
5. **Open an issue** on GitHub with:
   - What you were trying to do
   - What happened instead
   - Error messages (if any)
   - Screenshots of dashboard

## 🎯 Quick Command Reference

```bash
# Build and run dashboard
./gradlew :dashboard:run

# Build dashboard JAR
./gradlew :dashboard:jar

# Build everything (robot + dashboard)
./gradlew build

# List available tasks
./gradlew :dashboard:tasks

# Clean and rebuild
./gradlew :dashboard:clean :dashboard:build

# Run with debug output
./gradlew :dashboard:run --info
```

---

**Happy debugging and competing!** 🤖💜

For detailed documentation, see: `dashboard/README.md`
