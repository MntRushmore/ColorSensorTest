# Dashboard Visual Layout Guide 🎨

## What You'll See When You Run the Dashboard

```
╔══════════════════════════════════════════════════════════════════════════════════╗
║                         🤖 FRC ROBOT DASHBOARD                                   ║
║              Ball Sorting System • Real-Time Telemetry                           ║
║                          ● DISCONNECTED (Red)                                    ║
╚══════════════════════════════════════════════════════════════════════════════════╝
┌─────────────────────────────────────────────────────┬──────────────────────────┐
│                                                      │                          │
│  ┌────────────────────────────────────────────┐    │  🔗 CONNECTION           │
│  │ 🎨 COLOR SENSOR DATA                       │    │  ┌──────────────────┐   │
│  │                                             │    │  │ localhost        │   │
│  │  Proximity:          150                   │    │  └──────────────────┘   │
│  │  Red:                0.561                 │    │  [CONNECT TO ROBOT]      │
│  │  Green:              0.232                 │    │                          │
│  │  Blue:               0.114                 │    │  🧪 MANUAL TESTING       │
│  │  Confidence:         0.95                  │    │  Motor Speed:            │
│  │  Detected:           Red                   │    │  ├──────●────┤ 0.50     │
│  │                                             │    │                          │
│  └────────────────────────────────────────────┘    │  [TEST INTAKE]           │
│                                                      │  [TEST SHOOTER]          │
│  ┌────────────────────────────────────────────┐    │  [TEST REJECT]           │
│  │ 🌈 COLOR PREVIEW                           │    │  [⏹ STOP ALL]           │
│  │                                             │    │                          │
│  │       ┌─────────────────────────┐          │    │  ⚙️ CALIBRATION          │
│  │       │                         │          │    │  [🔴 CALIBRATE RED]      │
│  │       │    Current RGB Color    │          │    │  [🔵 CALIBRATE BLUE]     │
│  │       │    (Live Preview Box)   │          │    │  [🟢 CALIBRATE GREEN]    │
│  │       │                         │          │    │  [🟡 CALIBRATE YELLOW]   │
│  │       └─────────────────────────┘          │    │  [💾 SAVE CALIBRATION]   │
│  │                                             │    │                          │
│  └────────────────────────────────────────────┘    │  ⚡ CONFIGURATION        │
│                                                      │  Target Color:           │
├─────────────────────────────────────────────────────┤  [▼ BLUE    ]           │
│                                                      │  [SET TARGET]            │
│  ┌────────────────────────────────────────────┐    │                          │
│  │ 🤖 ROBOT STATE                             │    │  Proximity Threshold:    │
│  │                                             │    │  ├─────●──┤ 100         │
│  │  State:              IDLE                  │    │                          │
│  │  Ball Detected:      ✗ NO                  │    │  Confidence Threshold:   │
│  │  Last Color:         Blue                  │    │  ├────●───┤ 0.80        │
│  │  Target Color:       BLUE                  │    │                          │
│  │  Intake Enabled:     ✓ ENABLED             │    │                          │
│  │                                             │    │                          │
│  └────────────────────────────────────────────┘    │                          │
│                                                      │                          │
│  ┌────────────────────────────────────────────┐    │                          │
│  │ 📊 SYSTEM STATUS                           │    │                          │
│  │                                             │    │                          │
│  │  System operational                        │    │                          │
│  │  All subsystems ready                      │    │                          │
│  │  NetworkTables active                      │    │                          │
│  │                                             │    │                          │
│  └────────────────────────────────────────────┘    │                          │
│                                                      │                          │
└─────────────────────────────────────────────────────┴──────────────────────────┘
╔══════════════════════════════════════════════════════════════════════════════════╗
║ FRC Dashboard v1.0 | Ready                                                       ║
╚══════════════════════════════════════════════════════════════════════════════════╝
```

## Color Coding Guide

### Header
- **Title**: White text on purple gradient background
- **Connection Status**: 
  - `● CONNECTED` in **green** when connected
  - `● DISCONNECTED` in **red** when not connected

### Telemetry Panels
- **Panel Background**: Dark purple (#2D1B3D)
- **Panel Border**: Medium purple (#6A1B9A)
- **Panel Titles**: Light purple (#E0B0FF)
- **Label Text**: Bright purple (#BB86FC)
- **Value Text**: White (monospace font)
- **Highlighted Values**: Gold (#FFD700)

### Robot State Colors
The "State:" value changes color based on current state:
- **IDLE**: Light purple (#E0B0FF)
- **BALL_DETECTED**: Gold (#FFD700)
- **ROUTING_TO_SHOOTER**: Green (#4CAF50) - correct color ball
- **ROUTING_TO_REJECT**: Red (#FF6B6B) - wrong color ball
- **CLEARING**: Orange (#FFA500)

### Boolean Indicators
- **True/Enabled**: ✓ in **green** (#4CAF50)
- **False/Disabled**: ✗ in **red** (#FF6B6B)

### Buttons
- **Control Buttons**: Purple (#6A1B9A), hover brightens to (#9C27B0)
- **Test Buttons**: Light purple (#BB86FC) with dark text
- **Stop Button**: Red (#FF6B6B)
- **Calibrate Buttons**: Medium purple (#9C27B0)
- **Save Button**: Green (#4CAF50)

## Live Data Updates

When connected to the robot, you'll see values update in real-time:

### Color Sensor Panel (Updates ~10 times per second)
```
Proximity:          150  ← Changes as ball approaches
Red:                0.561 ← RGB values update continuously
Green:              0.232
Blue:               0.114
Confidence:         0.95  ← Match confidence
Detected:           Red   ← Detected color name
```

### Color Preview Box (Updates in real-time)
The colored rectangle shows the actual detected color:
- Empty/black when no ball detected
- Changes to actual RGB color when ball is near
- Helps visually verify calibration

### Robot State Panel (Updates immediately on state changes)
```
State:              BALL_DETECTED ← State machine status
Ball Detected:      ✓ YES         ← Proximity threshold triggered
Last Color:         Blue          ← Previous ball color
Target Color:       BLUE          ← Your configured target
Intake Enabled:     ✓ ENABLED     ← Intake subsystem status
```

## Control Panel (Right Sidebar)

### Connection Section
```
┌──────────────────────────────────┐
│ 🔗 CONNECTION                    │
│                                   │
│ ┌──────────────────────────────┐ │
│ │ localhost                    │ │ ← Enter robot IP here
│ └──────────────────────────────┘ │
│                                   │
│ [   CONNECT TO ROBOT   ]         │ ← Click to connect
└──────────────────────────────────┘
```

**What to enter**:
- `localhost` - For testing/simulation
- `10.2.54.2` - For team 254's robot (use your team number)
- `172.22.11.2` - For USB connection
- `roborio-254-frc.local` - For mDNS connection

### Testing Section
```
┌──────────────────────────────────┐
│ 🧪 MANUAL TESTING                │
│                                   │
│ Motor Speed:                      │
│ ├──────●────────┤ 0.50           │ ← Drag slider
│                                   │
│ [    TEST INTAKE    ]            │ ← Runs intake motor
│ [   TEST SHOOTER    ]            │ ← Runs shooter motor
│ [    TEST REJECT    ]            │ ← Runs reject motor
│ [     ⏹ STOP ALL     ]           │ ← Emergency stop (red)
└──────────────────────────────────┘
```

**How to use**:
1. Drag slider to set speed (0.0 = stop, 1.0 = full speed)
2. Click test button for desired motor
3. Motor runs at set speed while robot is in test mode
4. Click STOP ALL to immediately stop everything

### Calibration Section
```
┌──────────────────────────────────┐
│ ⚙️ CALIBRATION                   │
│                                   │
│ [  🔴 CALIBRATE RED   ]          │
│ [  🔵 CALIBRATE BLUE  ]          │
│ [  🟢 CALIBRATE GREEN ]          │
│ [  🟡 CALIBRATE YELLOW]          │
│                                   │
│ [  💾 SAVE CALIBRATION ]         │ ← Saves to robot
└──────────────────────────────────┘
```

**How to use**:
1. Place colored ball near sensor
2. Wait for RGB values to stabilize
3. Click corresponding color button
4. Repeat for all colors
5. Click SAVE to persist settings

### Configuration Section
```
┌──────────────────────────────────┐
│ ⚡ CONFIGURATION                  │
│                                   │
│ Target Color:                     │
│ [▼ BLUE            ]             │ ← Select from dropdown
│ [    SET TARGET     ]            │
│                                   │
│ Proximity Threshold:              │
│ ├─────●──────┤ 100               │ ← Ball detection sensitivity
│                                   │
│ Confidence Threshold:             │
│ ├────●───────┤ 0.80              │ ← Color match strictness
└──────────────────────────────────┘
```

**What it does**:
- **Target Color**: Choose which color balls go to shooter (others get rejected)
- **Proximity**: How close ball must be to detect (50-200)
- **Confidence**: Minimum match quality required (0.5-1.0)

## Example Scenarios

### Scenario 1: Ball Approaching Intake

**What you'll see**:
```
Proximity:          50 → 75 → 100 → 125 → 150
Ball Detected:      ✗ NO → ✗ NO → ✓ YES
State:              IDLE → BALL_DETECTED
Color Preview:      Black → Getting brighter → Full color
```

### Scenario 2: Correct Color Ball (Target = BLUE)

**What you'll see**:
```
State:              BALL_DETECTED → ROUTING_TO_SHOOTER → CLEARING → IDLE
Detected:           Blue
Last Color:         Blue
Ball Detected:      ✓ YES → ✓ YES → ✗ NO
State Color:        Gold → Green → Orange → Purple
```

Console output:
```
Ball detected! Color: Blue | Confidence: 0.95
✓ Correct color! Routing to SHOOTER
Ready for next ball!
```

### Scenario 3: Wrong Color Ball (Target = BLUE, Got RED)

**What you'll see**:
```
State:              BALL_DETECTED → ROUTING_TO_REJECT → CLEARING → IDLE
Detected:           Red
Last Color:         Red
State Color:        Gold → Red → Orange → Purple
```

Console output:
```
Ball detected! Color: Red | Confidence: 0.92
✗ Wrong color! Routing to REJECT
Ready for next ball!
```

### Scenario 4: Testing Motors

**What you'll see**:
```
1. Move slider to 0.75
   Speed Display: 0.75

2. Click TEST INTAKE
   Console: "Testing intake at speed: 0.75"

3. Click STOP ALL
   Console: "All motors stopped"
```

## Tips for Best Experience

### 1. Window Size
- **Minimum**: 1200x800 pixels
- **Recommended**: 1400x900 pixels
- **Ideal**: Full screen on 1920x1080 display

### 2. Monitor Console Output
The dashboard prints helpful messages to console:
- Connection status
- Button clicks
- NetworkTables data
- Error messages

### 3. Keep Driver Station Open
If using NetworkTables, keep FRC Driver Station open to:
- See robot code status
- Enable/disable robot
- View joystick status

### 4. Use Test Mode for Manual Testing
Put robot in **TEST** mode when using manual motor controls:
- Safer than enabled mode
- No need for driver station input
- Can test individual subsystems

## Next Steps

1. **Start the dashboard**: `./gradlew :dashboard:run`
2. **Connect to robot**: Enter IP and click connect
3. **Monitor telemetry**: Watch values update in real-time
4. **Test features**: Try manual controls and calibration
5. **Customize**: Modify theme or add new panels (see README.md)

---

**Enjoy your purple-themed FRC dashboard!** 🤖💜
