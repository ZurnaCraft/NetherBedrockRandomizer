package dev.msncakma.netherbedrockrandomizer.randomizer;

import dev.msncakma.netherbedrockrandomizer.NetherBedrockRandomizer;
import dev.msncakma.netherbedrockrandomizer.config.ConfigManager;
import dev.msncakma.netherbedrockrandomizer.util.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Core bedrock randomization logic
 * Handles the actual randomization of bedrock blocks in chunks
 * Optimized for high performance and minimal memory usage
 * 
 * @author msncakma
 */
public class BedrockRandomizer {

    private final NetherBedrockRandomizer plugin;
    private final ConfigManager config;

    public BedrockRandomizer(NetherBedrockRandomizer plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    /**
     * Process a chunk and randomize its bedrock layers
     * This is the main entry point for chunk processing
     * 
     * @param chunk The chunk to process
     * @return Number of blocks that were randomized
     */
    public int processChunk(Chunk chunk) {
        long startTime = System.currentTimeMillis();
        
        if (config.isDebug()) {
            plugin.getLogger().info("Processing chunk: " + ChunkUtil.formatChunkCoords(chunk));
        }
        
        // Get chunk-specific seed for consistent randomization
        long chunkSeed = ChunkUtil.getChunkSeed(chunk, config.getNumericSeed());
        
        // Count blocks changed
        int blocksChanged = 0;
        
        // Process bottom layers
        blocksChanged += processBottomLayers(chunk, chunkSeed);
        
        // Process top layers
        blocksChanged += processTopLayers(chunk, chunkSeed);
        
        // Update statistics
        long processingTime = System.currentTimeMillis() - startTime;
        plugin.incrementChunksProcessed();
        plugin.addBlocksRandomized(blocksChanged);
        plugin.addProcessingTime(processingTime);
        
        if (config.isDebug()) {
            plugin.getLogger().info(String.format(
                "Chunk %s processed: %d blocks changed in %dms",
                ChunkUtil.formatChunkCoords(chunk),
                blocksChanged,
                processingTime
            ));
        }
        
        return blocksChanged;
    }
    
    /**
     * Process bottom bedrock layers (Y=1 to Y=4 by default)
     */
    private int processBottomLayers(Chunk chunk, long chunkSeed) {
        int blocksChanged = 0;
        int protectedLayers = config.getProtectedBottomLayers();
        int randomizeLayers = config.getRandomizeBottomLayers();
        
        if (randomizeLayers == 0) {
            return 0;
        }
        
        // Calculate Y range
        int minY = protectedLayers;
        int maxY = protectedLayers + randomizeLayers - 1;
        
        if (config.isDebug()) {
            plugin.getLogger().info(String.format("Bottom layers: Y=%d to Y=%d", minY, maxY));
        }
        
        // Process each layer
        for (int y = minY; y <= maxY; y++) {
            blocksChanged += processLayer(chunk, y, chunkSeed);
        }
        
        return blocksChanged;
    }
    
    /**
     * Process top bedrock layers (Y=123 to Y=126 by default)
     */
    private int processTopLayers(Chunk chunk, long chunkSeed) {
        int blocksChanged = 0;
        int protectedLayers = config.getProtectedTopLayers();
        int randomizeLayers = config.getRandomizeTopLayers();
        
        if (randomizeLayers == 0) {
            return 0;
        }
        
        // Calculate Y range (from nether ceiling at Y=127)
        // If protected=1, randomize=4: should be Y=123,124,125,126
        int minY = 127 - protectedLayers - randomizeLayers + 1;
        int maxY = 127 - protectedLayers;
        
        if (config.isDebug()) {
            plugin.getLogger().info(String.format("Top layers: Y=%d to Y=%d (protected=%d, randomize=%d)", 
                minY, maxY, protectedLayers, randomizeLayers));
        }
        
        // Process each layer
        for (int y = minY; y <= maxY; y++) {
            blocksChanged += processLayer(chunk, y, chunkSeed);
        }
        
        return blocksChanged;
    }
    
    /**
     * Process a single Y layer in a chunk
     * Uses batch processing for optimal performance
     */
    private int processLayer(Chunk chunk, int y, long chunkSeed) {
        int blocksChanged = 0;
        double density = config.getBedrockDensity();
        
        // Use ThreadLocalRandom for better performance in multi-threaded environment (Folia)
        // Seed it with chunk-specific seed for consistency
        long layerSeed = chunkSeed + y;
        
        // Batch processing - collect blocks to change
        List<Block> blocksToSet = new ArrayList<>(256); // Pre-allocate for 16x16
        List<Material> materials = new ArrayList<>(256);
        
        // Iterate through all X and Z coordinates in chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Calculate position-specific seed
                long positionSeed = layerSeed + (x * 16 + z);
                
                // Determine if this position should be bedrock
                boolean shouldBeBedrock = shouldPlaceBedrock(positionSeed, density);
                
                Block block = chunk.getBlock(x, y, z);
                Material currentMaterial = block.getType();
                
                // Only change if necessary
                if (shouldBeBedrock && currentMaterial != Material.BEDROCK) {
                    blocksToSet.add(block);
                    materials.add(Material.BEDROCK);
                    blocksChanged++;
                } else if (!shouldBeBedrock && currentMaterial == Material.BEDROCK) {
                    blocksToSet.add(block);
                    materials.add(Material.NETHERRACK);
                    blocksChanged++;
                }
            }
        }
        
        // Apply changes in batch
        applyBlockChanges(blocksToSet, materials);
        
        return blocksChanged;
    }
    
    /**
     * Determine if a block should be bedrock based on seed and density
     * Uses a simple but effective pseudo-random algorithm
     * 
     * @param seed Position-specific seed
     * @param density Target bedrock density (0.0-1.0)
     * @return true if this position should be bedrock
     */
    private boolean shouldPlaceBedrock(long seed, double density) {
        // Use XorShift for better randomization
        // Mix the seed better to avoid patterns
        seed ^= (seed << 21);
        seed ^= (seed >>> 35);
        seed ^= (seed << 4);
        
        // Additional mixing for better distribution
        long hash = seed * 0x27d4eb2d;
        hash = (hash ^ (hash >>> 15)) * 0x85ebca6b;
        hash = (hash ^ (hash >>> 13)) * 0xc2b2ae35;
        hash = hash ^ (hash >>> 16);
        
        // Convert to 0.0-1.0 range using unsigned long
        double random = (double)(hash & 0x7FFFFFFFFFFFFFFFL) / (double)Long.MAX_VALUE;
        
        return random < density;
    }
    
    /**
     * Apply block changes in batch for optimal performance
     * Groups changes to minimize overhead
     */
    private void applyBlockChanges(List<Block> blocks, List<Material> materials) {
        int batchSize = config.getBatchSize();
        
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            Material material = materials.get(i);
            
            // Set block type without physics update for speed
            block.setType(material, false);
            
            // Optional: Add small pause between batches to prevent lag
            // Only if batch size is large
            if (batchSize > 0 && (i + 1) % batchSize == 0) {
                // Could add Thread.yield() here if needed, but usually not necessary
            }
        }
    }
    
    /**
     * Get estimated processing time for a chunk
     * Used for performance monitoring
     * 
     * @return Estimated milliseconds to process a chunk
     */
    public long getEstimatedProcessingTime() {
        long avgTime = plugin.getAverageProcessingTime();
        return avgTime > 0 ? avgTime : 10; // Default estimate: 10ms
    }
}
