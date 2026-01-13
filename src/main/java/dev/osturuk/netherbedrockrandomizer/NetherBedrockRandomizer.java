package dev.osturuk.netherbedrockrandomizer;

import dev.osturuk.netherbedrockrandomizer.config.ConfigManager;
import dev.osturuk.netherbedrockrandomizer.listener.ChunkListener;
import dev.osturuk.netherbedrockrandomizer.randomizer.BedrockRandomizer;
import dev.osturuk.netherbedrockrandomizer.util.FoliaUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * NetherBedrockRandomizer - High-performance bedrock randomizer for Folia servers
 * Prevents seed-based cheating by randomizing nether bedrock patterns
 * 
 * @author osturuk
 * @version 1.0.0
 */
public final class NetherBedrockRandomizer extends JavaPlugin {

    private static NetherBedrockRandomizer instance;
    private ConfigManager configManager;
    private BedrockRandomizer bedrockRandomizer;
    private ChunkListener chunkListener;
    
    // Performance statistics
    private long chunksProcessed = 0;
    private long blocksRandomized = 0;
    private long totalProcessingTime = 0;

    @Override
    public void onEnable() {
        instance = this;
        
        // ASCII Art Logo
        getLogger().info("================================================");
        getLogger().info("  _   _ ____  ____  ");
        getLogger().info(" | \\ | | __ )|  _ \\ ");
        getLogger().info(" |  \\| |  _ \\| |_) |");
        getLogger().info(" | |\\  | |_) |  _ < ");
        getLogger().info(" |_| \\_|____/|_| \\_\\");
        getLogger().info("    Nether Bedrock Randomizer");
        getLogger().info("================================================");
        
        // Check Folia compatibility
        if (!FoliaUtil.isFolia()) {
            getLogger().warning("================================================");
            getLogger().warning("WARNING: This plugin is designed for Folia!");
            getLogger().warning("Running on Paper/Spigot may cause issues.");
            getLogger().warning("For best performance, use Folia 1.21.4+");
            getLogger().warning("================================================");
        } else {
            getLogger().info("Folia detected! Running in optimized mode.");
        }
        
        // Initialize configuration
        try {
            configManager = new ConfigManager(this);
            configManager.loadConfig();
            getLogger().info("Configuration loaded successfully");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to load configuration!", e);
            disablePlugin();
            return;
        }
        
        // Check if plugin is enabled in config
        if (!configManager.isEnabled()) {
            getLogger().warning("Plugin is disabled in config.yml");
            getLogger().warning("Set 'settings.enabled: true' to activate");
            disablePlugin();
            return;
        }
        
        // Initialize bedrock randomizer
        try {
            bedrockRandomizer = new BedrockRandomizer(this);
            getLogger().info("Bedrock randomizer initialized");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize bedrock randomizer!", e);
            disablePlugin();
            return;
        }
        
        // Register chunk listener
        try {
            chunkListener = new ChunkListener(this);
            Bukkit.getPluginManager().registerEvents(chunkListener, this);
            getLogger().info("Chunk listener registered");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register chunk listener!", e);
            disablePlugin();
            return;
        }
        
        // Register command
        getCommand("nbr").setExecutor(new CommandHandler(this));
        
        // Print configuration summary
        getLogger().info("------------------------------------------------");
        getLogger().info("Configuration Summary:");
        getLogger().info("  Worlds: " + configManager.getEnabledWorlds());
        getLogger().info("  Bedrock Density: " + (configManager.getBedrockDensity() * 100) + "%");
        getLogger().info("  Bottom Layers: " + configManager.getRandomizeBottomLayers() + " randomized");
        getLogger().info("  Top Layers: " + configManager.getRandomizeTopLayers() + " randomized");
        getLogger().info("  Batch Size: " + configManager.getBatchSize() + " blocks");
        getLogger().info("  Processing Delay: " + configManager.getProcessingDelayTicks() + " ticks");
        getLogger().info("------------------------------------------------");
        
        getLogger().info("NetherBedrockRandomizer v" + getDescription().getVersion() + " enabled!");
        getLogger().info("Ready to prevent seed-based cheating!");
    }

    @Override
    public void onDisable() {
        // Print statistics
        if (chunksProcessed > 0) {
            getLogger().info("================================================");
            getLogger().info("Performance Statistics:");
            getLogger().info("  Chunks Processed: " + chunksProcessed);
            getLogger().info("  Blocks Randomized: " + blocksRandomized);
            getLogger().info("  Average Processing Time: " + (totalProcessingTime / chunksProcessed) + "ms per chunk");
            getLogger().info("================================================");
        }
        
        getLogger().info("NetherBedrockRandomizer disabled. Goodbye!");
        instance = null;
    }
    
    /**
     * Disable the plugin safely
     */
    private void disablePlugin() {
        getLogger().severe("Plugin failed to initialize properly and will be disabled");
        Bukkit.getPluginManager().disablePlugin(this);
    }
    
    /**
     * Reload plugin configuration
     */
    public boolean reload() {
        try {
            configManager.loadConfig();
            getLogger().info("Configuration reloaded successfully");
            return true;
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to reload configuration!", e);
            return false;
        }
    }
    
    // Performance tracking methods
    public void incrementChunksProcessed() {
        chunksProcessed++;
    }
    
    public void addBlocksRandomized(int count) {
        blocksRandomized += count;
    }
    
    public void addProcessingTime(long milliseconds) {
        totalProcessingTime += milliseconds;
    }
    
    // Getters
    public static NetherBedrockRandomizer getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public BedrockRandomizer getBedrockRandomizer() {
        return bedrockRandomizer;
    }
    
    public long getChunksProcessed() {
        return chunksProcessed;
    }
    
    public long getBlocksRandomized() {
        return blocksRandomized;
    }
    
    public long getAverageProcessingTime() {
        return chunksProcessed > 0 ? totalProcessingTime / chunksProcessed : 0;
    }
}
