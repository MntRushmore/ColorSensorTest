package frc.dashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NetworkTables Client for Robot Communication
 * 
 * Simplified version using polling instead of event listeners for compatibility.
 * This class manages the connection to the robot's NetworkTables server
 * and provides methods to read/write telemetry data in real-time.
 * 
 * @author FRC Dashboard Team
 */
public class NetworkTablesClient {
    
    // Default NetworkTables port for NT4 protocol
    public static final int kDefaultPort4 = 5810;
    
    // NetworkTables instance
    private NetworkTableInstance inst;
    
    // Main SmartDashboard table
    private NetworkTable smartDashboard;
    
    // Connection status
    private boolean connected = false;
    
    // Subscription tracking
    private Map<String, SubscriptionInfo> subscriptions = new HashMap<>();
    private Timer pollTimer;
    
    // Subscription info class
    private static class SubscriptionInfo {
        Consumer<Object> callback;
        Object lastValue;
        
        SubscriptionInfo(Consumer<Object> callback) {
            this.callback = callback;
        }
    }
    
    /**
     * Initialize NetworkTables client and connect to robot
     */
    public NetworkTablesClient() {
        this("localhost", kDefaultPort4);
    }
    
    /**
     * Initialize with custom robot IP/hostname
     */
    public NetworkTablesClient(String serverAddress, int port) {
        try {
            inst = NetworkTableInstance.getDefault();
            inst.startClient4("FRC Dashboard");
            inst.setServer(serverAddress, port);
            smartDashboard = inst.getTable("SmartDashboard");
            
            System.out.println("NetworkTables client initialized");
            System.out.println("Connecting to: " + serverAddress + ":" + port);
            
            // Start polling for subscriptions
            startPolling();
            
        } catch (Exception e) {
            System.err.println("Failed to initialize NetworkTables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Connect to a specific robot team number
     */
    public void connectToTeam(int teamNumber) {
        inst.setServerTeam(teamNumber);
        System.out.println("Connecting to team " + teamNumber);
    }
    
    /**
     * Check if connected to robot
     */
    public boolean isConnected() {
        return inst.isConnected();
    }
    
    // ==================== READ METHODS ====================
    
    public double getNumber(String key, double defaultValue) {
        return smartDashboard.getEntry(key).getDouble(defaultValue);
    }
    
    public String getString(String key, String defaultValue) {
        return smartDashboard.getEntry(key).getString(defaultValue);
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        return smartDashboard.getEntry(key).getBoolean(defaultValue);
    }
    
    // ==================== WRITE METHODS ====================
    
    public void setNumber(String key, double value) {
        smartDashboard.getEntry(key).setDouble(value);
    }
    
    public void setString(String key, String value) {
        smartDashboard.getEntry(key).setString(value);
    }
    
    public void setBoolean(String key, boolean value) {
        smartDashboard.getEntry(key).setBoolean(value);
    }
    
    // ==================== SUBSCRIPTION METHODS (Polling-based) ====================
    
    /**
     * Subscribe to number value changes
     */
    public void subscribeNumber(String key, Consumer<Double> callback) {
        subscriptions.put(key, new SubscriptionInfo(obj -> callback.accept((Double) obj)));
    }
    
    /**
     * Subscribe to string value changes
     */
    public void subscribeString(String key, Consumer<String> callback) {
        subscriptions.put(key, new SubscriptionInfo(obj -> callback.accept((String) obj)));
    }
    
    /**
     * Subscribe to boolean value changes
     */
    public void subscribeBoolean(String key, Consumer<Boolean> callback) {
        subscriptions.put(key, new SubscriptionInfo(obj -> callback.accept((Boolean) obj)));
    }
    
    /**
     * Unsubscribe from a specific entry
     */
    public void unsubscribe(String key) {
        subscriptions.remove(key);
    }
    
    /**
     * Start polling for subscription updates
     */
    private void startPolling() {
        pollTimer = new Timer(true);
        pollTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pollSubscriptions();
            }
        }, 0, 100); // Poll every 100ms
    }
    
    /**
     * Poll all subscriptions for value changes
     */
    private void pollSubscriptions() {
        for (Map.Entry<String, SubscriptionInfo> entry : subscriptions.entrySet()) {
            String key = entry.getKey();
            SubscriptionInfo sub = entry.getValue();
            
            try {
                // Try to get value as different types
                Object newValue = null;
                var ntEntry = smartDashboard.getEntry(key);
                
                // Check if entry exists and has a value
                if (ntEntry.exists()) {
                    var value = ntEntry.getValue();
                    if (value != null) {
                        if (value.isDouble()) {
                            newValue = value.getDouble();
                        } else if (value.isString()) {
                            newValue = value.getString();
                        } else if (value.isBoolean()) {
                            newValue = value.getBoolean();
                        }
                    }
                }
                
                // Only callback if value changed
                if (newValue != null && !newValue.equals(sub.lastValue)) {
                    sub.lastValue = newValue;
                    sub.callback.accept(newValue);
                }
            } catch (Exception e) {
                // Ignore polling errors for individual entries
            }
        }
    }
    
    /**
     * Get raw NetworkTable for custom access
     */
    public NetworkTable getTable(String tableName) {
        return inst.getTable(tableName);
    }
    
    /**
     * Flush all pending changes immediately
     */
    public void flush() {
        inst.flush();
    }
    
    /**
     * Disconnect from robot and cleanup resources
     */
    public void disconnect() {
        System.out.println("Disconnecting from NetworkTables...");
        
        if (pollTimer != null) {
            pollTimer.cancel();
        }
        
        subscriptions.clear();
        inst.stopClient();
        connected = false;
        
        System.out.println("NetworkTables disconnected");
    }
}
