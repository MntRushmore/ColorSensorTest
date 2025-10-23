package frc.dashboard;

import edu.wpi.first.util.CombinedRuntimeLoader;
import java.io.IOException;
import java.util.List;

/**
 * Helper class to manually load WPILib JNI libraries before NetworkTables initialization.
 * This ensures native libraries are loaded using WPILib's CombinedRuntimeLoader
 * instead of System.loadLibrary().
 */
public class JNILoader {
    
    private static boolean loaded = false;
    
    /**
     * Load all required WPILib native libraries.
     * Must be called before any NetworkTables usage.
     */
    public static synchronized void loadLibraries() {
        if (loaded) {
            return;
        }
        
        try {
            System.out.println("Loading WPILib native libraries...");
            
            // Load wpiutil first (ntcore depends on it)
            List<String> wpiutilFiles = CombinedRuntimeLoader.extractLibraries(
                JNILoader.class, 
                "/wpiutil-jni.json"
            );
            
            for (String file : wpiutilFiles) {
                if (file.contains("wpiutil")) {
                    System.out.println("Loading wpiutil from: " + file);
                    System.load(file);
                }
            }
            
            // Load ntcore
            List<String> ntcoreFiles = CombinedRuntimeLoader.extractLibraries(
                JNILoader.class,
                "/ntcore-jni.json"
            );
            
            for (String file : ntcoreFiles) {
                if (file.contains("ntcore")) {
                    System.out.println("Loading ntcore from: " + file);
                    System.load(file);
                }
            }
            
            loaded = true;
            System.out.println("Successfully loaded all native libraries!");
            
        } catch (IOException e) {
            System.err.println("Failed to load native libraries: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not load WPILib native libraries", e);
        }
    }
}
