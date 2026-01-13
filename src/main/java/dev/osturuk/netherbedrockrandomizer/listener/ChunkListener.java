package dev.osturuk.netherbedrockrandomizer.listener;

import dev.osturuk.netherbedrockrandomizer.NetherBedrockRandomizer;
import dev.osturuk.netherbedrockrandomizer.config.ConfigManager;
import dev.osturuk.netherbedrockrandomizer.randomizer.BedrockRandomizer;
import dev.osturuk.netherbedrockrandomizer.util.ChunkUtil;
import dev.osturuk.netherbedrockrandomizer.util.FoliaUtil;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Listener for chunk events
 * Handles chunk loading and triggers bedrock randomization
 * Uses Folia-compatible scheduling for optimal performance
 * 
 * @author osturuk
 */
public class ChunkListener implements Listener {

    private final NetherBedrockRandomizer plugin;
    private final ConfigManager config;
    private final BedrockRandomizer randomizer;

    public ChunkListener(NetherBedrockRandomizer plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.randomizer = plugin.getBedrockRandomizer();
    }

    /**
     * Handle chunk load events
     * This is triggered when a chunk is loaded into memory
     * 
     * Priority: MONITOR - runs after all other plugins
     * This ensures we modify chunks after other plugins have finished with them
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        
        // Quick checks to filter out chunks we don't care about
        
        // 1. Check if chunk is in Nether
        if (!ChunkUtil.isNetherChunk(chunk)) {
            return;
        }
        
        // 2. Check if world is enabled in config
        if (!config.isWorldEnabled(chunk.getWorld().getName())) {
            return;
        }
        
        // 3. Check if we should only process new chunks
        if (config.isOnlyNewChunks() && !event.isNewChunk()) {
            if (config.isDebug()) {
                plugin.getLogger().info("Skipping existing chunk: " + ChunkUtil.formatChunkCoords(chunk));
            }
            return;
        }
        
        // 4. Check if plugin is enabled
        if (!config.isEnabled()) {
            return;
        }
        
        if (config.isDebug()) {
            plugin.getLogger().info("Chunk loaded: " + ChunkUtil.formatChunkCoords(chunk) + 
                                   " (new: " + event.isNewChunk() + ")");
        }
        
        // Process the chunk using Folia-compatible scheduling
        scheduleChunkProcessing(chunk);
    }
    
    /**
     * Schedule chunk processing using Folia's region scheduler
     * This ensures thread-safe operation in Folia's multi-threaded environment
     */
    private void scheduleChunkProcessing(Chunk chunk) {
        int delayTicks = config.getProcessingDelayTicks();
        
        if (delayTicks > 0) {
            // Schedule with delay
            FoliaUtil.runForChunkLater(plugin, chunk, () -> processChunk(chunk), delayTicks);
        } else {
            // Process immediately (but still scheduled to ensure thread safety)
            FoliaUtil.runForChunk(plugin, chunk, () -> processChunk(chunk));
        }
    }
    
    /**
     * Process a chunk and randomize its bedrock
     * This runs in the appropriate region thread (Folia) or main thread (Bukkit)
     */
    private void processChunk(Chunk chunk) {
        try {
            // Verify chunk is still loaded
            if (!chunk.isLoaded()) {
                if (config.isDebug()) {
                    plugin.getLogger().warning("Chunk unloaded before processing: " + 
                                              ChunkUtil.formatChunkCoords(chunk));
                }
                return;
            }
            
            // Perform the randomization
            int blocksChanged = randomizer.processChunk(chunk);
            
            if (config.isDebug()) {
                plugin.getLogger().info(String.format(
                    "Processed %s: %d blocks changed",
                    ChunkUtil.formatChunkCoords(chunk),
                    blocksChanged
                ));
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error processing chunk " + 
                                     ChunkUtil.formatChunkCoords(chunk) + ": " + 
                                     e.getMessage());
            e.printStackTrace();
        }
    }
}
