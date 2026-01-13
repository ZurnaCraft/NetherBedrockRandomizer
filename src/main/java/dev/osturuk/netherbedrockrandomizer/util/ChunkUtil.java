package dev.osturuk.netherbedrockrandomizer.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Utility class for chunk-related operations
 * Provides helper methods for chunk processing and validation
 * 
 * @author osturuk
 */
public class ChunkUtil {

    /**
     * Check if a world is a Nether world
     * 
     * @param world The world to check
     * @return true if the world is a Nether dimension
     */
    public static boolean isNetherWorld(World world) {
        return world.getEnvironment() == World.Environment.NETHER;
    }
    
    /**
     * Check if a chunk is in a Nether world
     * 
     * @param chunk The chunk to check
     * @return true if the chunk is in a Nether dimension
     */
    public static boolean isNetherChunk(Chunk chunk) {
        return isNetherWorld(chunk.getWorld());
    }
    
    /**
     * Check if a Y coordinate is the bottom bedrock layer (Y=0)
     * 
     * @param y The Y coordinate
     * @return true if Y=0
     */
    public static boolean isBottomLayer(int y) {
        return y == 0;
    }
    
    /**
     * Check if a Y coordinate is the top bedrock layer (Y=127 in Nether)
     * 
     * @param y The Y coordinate
     * @return true if Y=127
     */
    public static boolean isTopLayer(int y) {
        return y == 127;
    }
    
    /**
     * Check if a Y coordinate should be randomized (bottom layers)
     * Default: Y=1 to Y=4
     * 
     * @param y The Y coordinate
     * @param protectedLayers Number of protected layers from bottom
     * @param randomizeLayers Number of layers to randomize
     * @return true if this Y level should be randomized
     */
    public static boolean shouldRandomizeBottom(int y, int protectedLayers, int randomizeLayers) {
        int minY = protectedLayers;
        int maxY = protectedLayers + randomizeLayers - 1;
        return y >= minY && y <= maxY;
    }
    
    /**
     * Check if a Y coordinate should be randomized (top layers)
     * Default: Y=123 to Y=126
     * 
     * @param y The Y coordinate
     * @param protectedLayers Number of protected layers from top
     * @param randomizeLayers Number of layers to randomize
     * @return true if this Y level should be randomized
     */
    public static boolean shouldRandomizeTop(int y, int protectedLayers, int randomizeLayers) {
        int maxY = 127 - protectedLayers - 1;
        int minY = maxY - randomizeLayers + 1;
        return y >= minY && y <= maxY;
    }
    
    /**
     * Get a unique seed for a chunk based on its coordinates
     * This ensures consistent randomization for the same chunk
     * 
     * @param chunk The chunk
     * @param baseSeed Base seed from configuration
     * @return A unique seed for this chunk
     */
    public static long getChunkSeed(Chunk chunk, long baseSeed) {
        int x = chunk.getX();
        int z = chunk.getZ();
        
        // Mix chunk coordinates with base seed
        // Using a simple but effective mixing algorithm
        long seed = baseSeed;
        seed = seed * 31L + x;
        seed = seed * 31L + z;
        seed ^= (seed >>> 32);
        
        return seed;
    }
    
    /**
     * Get center location of a chunk
     * 
     * @param chunk The chunk
     * @param y The Y coordinate
     * @return Center location of the chunk at specified Y
     */
    public static Location getChunkCenter(Chunk chunk, int y) {
        return chunk.getBlock(8, y, 8).getLocation();
    }
    
    /**
     * Format chunk coordinates as a string
     * 
     * @param chunk The chunk
     * @return Formatted string like "[world] (x, z)"
     */
    public static String formatChunkCoords(Chunk chunk) {
        return String.format("[%s] (%d, %d)", 
            chunk.getWorld().getName(), 
            chunk.getX(), 
            chunk.getZ());
    }
}
