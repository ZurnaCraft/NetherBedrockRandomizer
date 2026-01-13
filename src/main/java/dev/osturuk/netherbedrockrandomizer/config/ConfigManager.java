package dev.osturuk.netherbedrockrandomizer.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Configuration manager for NetherBedrockRandomizer
 * Handles loading and accessing configuration values
 * 
 * @author osturuk
 */
public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    
    // Cached configuration values for performance
    private boolean enabled;
    private List<String> enabledWorlds;
    private double bedrockDensity;
    private int protectedBottomLayers;
    private int protectedTopLayers;
    private int randomizeBottomLayers;
    private int randomizeTopLayers;
    private String customSeed;
    private boolean debug;
    private int batchSize;
    private boolean onlyNewChunks;
    private int processingDelayTicks;
    
    // Computed seed value
    private long numericSeed;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Load and cache configuration values
     */
    public void loadConfig() {
        // Save default config if not exists
        plugin.saveDefaultConfig();
        
        // Reload config from disk
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Load and cache values
        enabled = config.getBoolean("settings.enabled", true);
        enabledWorlds = config.getStringList("settings.worlds");
        bedrockDensity = config.getDouble("settings.bedrock-density", 0.85);
        protectedBottomLayers = config.getInt("settings.protected-bottom-layers", 1);
        protectedTopLayers = config.getInt("settings.protected-top-layers", 1);
        randomizeBottomLayers = config.getInt("settings.randomize-bottom-layers", 4);
        randomizeTopLayers = config.getInt("settings.randomize-top-layers", 4);
        customSeed = config.getString("settings.custom-seed", "NetherBedrockRandomizer-2026-CHANGE-THIS");
        debug = config.getBoolean("settings.debug", false);
        batchSize = config.getInt("performance.batch-size", 256);
        onlyNewChunks = config.getBoolean("performance.only-new-chunks", true);
        processingDelayTicks = config.getInt("performance.processing-delay-ticks", 0);
        
        // Validate and clamp values
        validateAndClampValues();
        
        // Compute numeric seed from string seed
        numericSeed = customSeed.hashCode();
        
        if (debug) {
            plugin.getLogger().info("Configuration loaded:");
            plugin.getLogger().info("  Enabled: " + enabled);
            plugin.getLogger().info("  Worlds: " + enabledWorlds);
            plugin.getLogger().info("  Bedrock Density: " + bedrockDensity);
            plugin.getLogger().info("  Numeric Seed: " + numericSeed);
        }
    }
    
    /**
     * Validate and clamp configuration values to safe ranges
     */
    private void validateAndClampValues() {
        // Clamp bedrock density between 0.0 and 1.0
        if (bedrockDensity < 0.0) {
            plugin.getLogger().warning("bedrock-density < 0.0, setting to 0.0");
            bedrockDensity = 0.0;
        } else if (bedrockDensity > 1.0) {
            plugin.getLogger().warning("bedrock-density > 1.0, setting to 1.0");
            bedrockDensity = 1.0;
        }
        
        // Ensure positive layer counts
        if (protectedBottomLayers < 1) {
            plugin.getLogger().warning("protected-bottom-layers < 1, setting to 1");
            protectedBottomLayers = 1;
        }
        
        if (protectedTopLayers < 1) {
            plugin.getLogger().warning("protected-top-layers < 1, setting to 1");
            protectedTopLayers = 1;
        }
        
        if (randomizeBottomLayers < 0) {
            plugin.getLogger().warning("randomize-bottom-layers < 0, setting to 0");
            randomizeBottomLayers = 0;
        }
        
        if (randomizeTopLayers < 0) {
            plugin.getLogger().warning("randomize-top-layers < 0, setting to 0");
            randomizeTopLayers = 0;
        }
        
        // Clamp batch size to reasonable values
        if (batchSize < 1) {
            plugin.getLogger().warning("batch-size < 1, setting to 64");
            batchSize = 64;
        } else if (batchSize > 4096) {
            plugin.getLogger().warning("batch-size > 4096, setting to 4096");
            batchSize = 4096;
        }
        
        // Clamp processing delay
        if (processingDelayTicks < 0) {
            plugin.getLogger().warning("processing-delay-ticks < 0, setting to 0");
            processingDelayTicks = 0;
        }
        
        // Ensure worlds list is not empty
        if (enabledWorlds.isEmpty()) {
            plugin.getLogger().warning("No worlds configured, adding 'world_nether'");
            enabledWorlds = List.of("world_nether");
        }
    }
    
    /**
     * Get a message from config with color codes translated
     */
    public String getMessage(String path) {
        String message = config.getString("messages." + path, "");
        return translateColorCodes(message);
    }
    
    /**
     * Get a message with prefix
     */
    public String getMessageWithPrefix(String path) {
        String prefix = getMessage("prefix");
        String message = getMessage(path);
        return prefix + " " + message;
    }
    
    /**
     * Translate color codes (&) to section signs (§)
     */
    private String translateColorCodes(String text) {
        return text.replace("&", "§");
    }

    // Getters
    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getEnabledWorlds() {
        return enabledWorlds;
    }
    
    public boolean isWorldEnabled(String worldName) {
        return enabledWorlds.contains(worldName);
    }

    public double getBedrockDensity() {
        return bedrockDensity;
    }

    public int getProtectedBottomLayers() {
        return protectedBottomLayers;
    }

    public int getProtectedTopLayers() {
        return protectedTopLayers;
    }

    public int getRandomizeBottomLayers() {
        return randomizeBottomLayers;
    }

    public int getRandomizeTopLayers() {
        return randomizeTopLayers;
    }

    public String getCustomSeed() {
        return customSeed;
    }
    
    public long getNumericSeed() {
        return numericSeed;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public boolean isOnlyNewChunks() {
        return onlyNewChunks;
    }

    public int getProcessingDelayTicks() {
        return processingDelayTicks;
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
