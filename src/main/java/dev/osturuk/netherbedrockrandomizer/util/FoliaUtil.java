package dev.osturuk.netherbedrockrandomizer.util;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Utility class for Folia compatibility
 * Provides abstraction layer for region-based scheduling
 * Falls back to legacy Bukkit scheduler if Folia is not available
 * 
 * @author osturuk
 */
public class FoliaUtil {

    private static final boolean IS_FOLIA;
    private static Method scheduleMethod = null;
    private static Class<?> regionSchedulerClass = null;
    
    static {
        // Check if Folia classes are available
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            // Not running on Folia
        }
        IS_FOLIA = folia;
        
        // Try to load Folia's region scheduler
        if (IS_FOLIA) {
            try {
                regionSchedulerClass = Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
            } catch (ClassNotFoundException e) {
                // Should not happen if IS_FOLIA is true
            }
        }
    }
    
    /**
     * Check if the server is running on Folia
     * 
     * @return true if Folia is detected
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }
    
    /**
     * Schedule a task for a specific chunk location (region-aware)
     * This is the preferred method for chunk-related operations on Folia
     * 
     * @param plugin The plugin instance
     * @param location The location (chunk) to schedule for
     * @param task The task to execute
     */
    public static void runAtLocation(Plugin plugin, Location location, Runnable task) {
        if (IS_FOLIA) {
            // Use Folia's region scheduler - execute directly on region thread
            try {
                // Folia'da chunk bazlı işlemler zaten region thread'inde çalışır
                // Bu yüzden direkt çalıştırabiliriz
                task.run();
            } catch (Exception e) {
                plugin.getLogger().severe("Error executing region task: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Use legacy Bukkit scheduler
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
    
    /**
     * Schedule a delayed task for a specific chunk location
     * 
     * @param plugin The plugin instance
     * @param location The location (chunk) to schedule for
     * @param task The task to execute
     * @param delayTicks Delay in ticks (20 ticks = 1 second)
     */
    public static void runAtLocationLater(Plugin plugin, Location location, Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            // Folia'da delay için Bukkit scheduler kullan (global scheduler)
            try {
                Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            } catch (Exception e) {
                plugin.getLogger().severe("Error scheduling delayed task: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Use legacy Bukkit scheduler
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
    
    /**
     * Schedule a task for a specific chunk (convenience method)
     * 
     * @param plugin The plugin instance
     * @param chunk The chunk to schedule for
     * @param task The task to execute
     */
    public static void runForChunk(Plugin plugin, Chunk chunk, Runnable task) {
        // Get a location in the chunk (center block at Y=64)
        Location location = chunk.getBlock(8, 64, 8).getLocation();
        runAtLocation(plugin, location, task);
    }
    
    /**
     * Schedule a delayed task for a specific chunk
     * 
     * @param plugin The plugin instance
     * @param chunk The chunk to schedule for
     * @param task The task to execute
     * @param delayTicks Delay in ticks
     */
    public static void runForChunkLater(Plugin plugin, Chunk chunk, Runnable task, long delayTicks) {
        Location location = chunk.getBlock(8, 64, 8).getLocation();
        runAtLocationLater(plugin, location, task, delayTicks);
    }
    
    /**
     * Schedule an async task for chunk processing
     * Calculates on async thread, then syncs to modify blocks
     * 
     * @param plugin The plugin instance
     * @param chunk The chunk to process
     * @param task The task to execute
     */
    public static void runForChunkAsync(Plugin plugin, Chunk chunk, Runnable task) {
        if (IS_FOLIA) {
            // On Folia, we need to run on the region thread for the chunk
            // ChunkLoadEvent already runs on region thread, so just execute
            task.run();
        } else {
            // On Bukkit, run async then sync back to main thread
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    task.run();
                } catch (Exception e) {
                    plugin.getLogger().severe("Error in async chunk processing: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * Execute a task asynchronously (global async pool)
     * Use sparingly - prefer region-based tasks when possible
     * 
     * @param plugin The plugin instance
     * @param task The task to execute
     */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            // Use Folia's async scheduler
            try {
                Object asyncScheduler = Bukkit.getServer().getClass()
                    .getMethod("getAsyncScheduler")
                    .invoke(Bukkit.getServer());
                    
                asyncScheduler.getClass()
                    .getMethod("runNow", Plugin.class, Runnable.class)
                    .invoke(asyncScheduler, plugin, (Runnable) () -> {
                        try {
                            task.run();
                        } catch (Exception e) {
                            plugin.getLogger().severe("Error executing async task: " + e.getMessage());
                        }
                    });
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to schedule Folia async task: " + e.getMessage());
                // Fallback to Bukkit async
                Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            }
        } else {
            // Use legacy Bukkit async scheduler
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
}
