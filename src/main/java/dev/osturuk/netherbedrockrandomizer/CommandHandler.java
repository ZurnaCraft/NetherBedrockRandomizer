package dev.osturuk.netherbedrockrandomizer;

import dev.osturuk.netherbedrockrandomizer.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Command handler for /nbr command
 * Provides admin commands for managing the plugin
 * 
 * @author osturuk
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final NetherBedrockRandomizer plugin;
    private final ConfigManager config;

    public CommandHandler(NetherBedrockRandomizer plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("netherbedrockrandomizer.admin")) {
            sender.sendMessage(config.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        // No arguments - show help
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        // Handle subcommands
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
                
            case "info":
                handleInfo(sender);
                break;
                
            case "stats":
                handleStats(sender);
                break;
                
            case "help":
                sendHelp(sender);
                break;
                
            default:
                sender.sendMessage(config.getMessageWithPrefix("reload-error"));
                sender.sendMessage("§cUnknown subcommand. Use /nbr help");
                break;
        }
        
        return true;
    }
    
    /**
     * Handle /nbr reload command
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("netherbedrockrandomizer.reload")) {
            sender.sendMessage(config.getMessageWithPrefix("no-permission"));
            return;
        }
        
        sender.sendMessage(config.getMessage("prefix") + " §7Reloading configuration...");
        
        boolean success = plugin.reload();
        
        if (success) {
            sender.sendMessage(config.getMessageWithPrefix("reload-success"));
        } else {
            sender.sendMessage(config.getMessageWithPrefix("reload-error"));
        }
    }
    
    /**
     * Handle /nbr info command
     */
    private void handleInfo(CommandSender sender) {
        if (!sender.hasPermission("netherbedrockrandomizer.info")) {
            sender.sendMessage(config.getMessageWithPrefix("no-permission"));
            return;
        }
        
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§6§l NetherBedrockRandomizer");
        sender.sendMessage("");
        sender.sendMessage(config.getMessage("info.version"));
        sender.sendMessage(config.getMessage("info.author"));
        sender.sendMessage(config.getMessage("info.folia-compatible"));
        sender.sendMessage(config.getMessage("info.performance"));
        sender.sendMessage("");
        sender.sendMessage("§7Status: " + (config.isEnabled() ? "§aEnabled" : "§cDisabled"));
        sender.sendMessage("§7Worlds: §f" + config.getEnabledWorlds());
        sender.sendMessage("§7Bedrock Density: §f" + (config.getBedrockDensity() * 100) + "%");
        sender.sendMessage("§7Bottom Layers: §f" + config.getRandomizeBottomLayers() + " randomized");
        sender.sendMessage("§7Top Layers: §f" + config.getRandomizeTopLayers() + " randomized");
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Handle /nbr stats command
     */
    private void handleStats(CommandSender sender) {
        if (!sender.hasPermission("netherbedrockrandomizer.info")) {
            sender.sendMessage(config.getMessageWithPrefix("no-permission"));
            return;
        }
        
        long chunksProcessed = plugin.getChunksProcessed();
        long blocksRandomized = plugin.getBlocksRandomized();
        long avgProcessingTime = plugin.getAverageProcessingTime();
        
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§6§l Performance Statistics");
        sender.sendMessage("");
        sender.sendMessage("§7Chunks Processed: §f" + chunksProcessed);
        sender.sendMessage("§7Blocks Randomized: §f" + blocksRandomized);
        
        if (chunksProcessed > 0) {
            sender.sendMessage("§7Average Processing Time: §f" + avgProcessingTime + "ms per chunk");
            sender.sendMessage("§7Average Blocks per Chunk: §f" + (blocksRandomized / chunksProcessed));
        } else {
            sender.sendMessage("§7No chunks processed yet");
        }
        
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Send help message
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§6§l NetherBedrockRandomizer Commands");
        sender.sendMessage("");
        sender.sendMessage("§7/nbr reload §8- §fReload configuration");
        sender.sendMessage("§7/nbr info §8- §fShow plugin information");
        sender.sendMessage("§7/nbr stats §8- §fShow performance statistics");
        sender.sendMessage("§7/nbr help §8- §fShow this help message");
        sender.sendMessage("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Suggest subcommands
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
            if ("info".startsWith(args[0].toLowerCase())) completions.add("info");
            if ("stats".startsWith(args[0].toLowerCase())) completions.add("stats");
            if ("help".startsWith(args[0].toLowerCase())) completions.add("help");
        }
        
        return completions;
    }
}
