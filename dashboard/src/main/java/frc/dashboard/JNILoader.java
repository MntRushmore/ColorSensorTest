package frc.dashboard;

import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.WPIUtilJNI;

/**
 * Helper class to force-load WPILib JNI libraries before NetworkTables initialization.
 * 
 * The issue: NetworkTableInstance.getDefault() triggers a static initializer that uses
 * System.loadLibrary() which fails because libraries aren't in java.library.path.
 * 
 * The solution: Call the JNI classes' static initializers explicitly, which use
 * WPILib's CombinedRuntimeLoader to properly extract and load from classpath JARs.
 */
public class JNILoader {
    
    private static boolean loaded = false;
    
    /**
     * Load all required WPILib native libraries by triggering static initializers.
     * Must be called before any NetworkTables usage.
     */
    public static synchronized void loadLibraries() {
        if (loaded) {
            return;
        }
        
        try {
            System.out.println("Loading WPILib native libraries...");
            
            // Force load WPIUtil JNI (must be first - ntcore depends on it)
            // This triggers WPIUtilJNI's static initializer which uses CombinedRuntimeLoader
            Class.forName("edu.wpi.first.util.WPIUtilJNI");
            System.out.println("✓ WPIUtil JNI loaded");
            
            // Force load NetworkTables JNI
            // This triggers NetworkTablesJNI's static initializer
            Class.forName("edu.wpi.first.networktables.NetworkTablesJNI");
            System.out.println("✓ NetworkTables JNI loaded");
            
            loaded = true;
            System.out.println("Successfully loaded all native libraries!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JNI classes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not load WPILib JNI classes", e);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native libraries: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not load WPILib native libraries", e);
        }
    }
}
