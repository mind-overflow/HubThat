package wtf.beatrice.hubthat.commands;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.utils.*;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;


public class SpawnCommand  implements CommandExecutor
{

    // Initialize the debugger so I can debug the plugin.
    private static final Debugger debugger = new Debugger(SpawnCommand.class.getName());


    // Initialize the plugin variable so we can access all of the plugin's data.
    private final HubThat plugin;

    // Constructor to actually give "plugin" a value.
    public SpawnCommand(HubThat givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {


        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        // Store the commandSender name for easy access.
        String username = commandSender.getName();

        // If the command comes from Console, stop it and give a warning.
        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }

        if(args.length > 1)
        {
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT_OTHERS))
            {
                String teleportingPlayerName = args[1];
                String worldName = args[0];

                Player teleportPlayer = plugin.getServer().getPlayer(teleportingPlayerName);
                if(teleportPlayer == null)
                {
                    String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_PLAYER_OFFLINE, true).replace("%player%", teleportingPlayerName);
                    commandSender.sendMessage(errorMessage);
                    return true;
                }

                if(plugin.getServer().getWorld(worldName) == null)
                {
                    MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.ERROR_SPAWN_NOT_SET);
                    return true;
                }

                teleportToSpawn(commandSender, teleportPlayer, worldName);
                return true;
            }
            else
            {
                // Warn the player he doesn't have permissions to teleport others to the hub.
                String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT_OTHERS.permission);
                commandSender.sendMessage(errorMessage);
                return true;
            }
        }

        // Check if the player has permission to teleport to the spawn.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT))
        {


            // Check if the player has permission to skip the teleport delay.
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.NO_SPAWN_DELAY))
            {

                // Run method to check if there are any args and teleport the player accordingly.
                checkArgsAndTeleport(args, commandSender);

                return true;
            }
            else
            {
                // Check if he's not already teleporting.
                if(!PluginCache.teleporting.contains(username))
                {
                    if(args.length > 0)
                    {
                        if(!PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_TELEPORT_ANOTHER_WORLD))
                        {
                            // Tell the player he has no permission to teleport to another world's spawn.
                            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT_ANOTHER_WORLD.permission);
                            commandSender.sendMessage(errorMessage);
                            return true;
                        }

                        if(plugin.getServer().getWorld(args[0]) == null)
                        {
                            String erroMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_WORLD_NOT_EXISTING, true).replace("%w%", args[0]);
                            commandSender.sendMessage(erroMessage);
                            return true;
                        }
                    }


                    // Load the teleportation delay.
                    int delay = FileUtils.FileType.CONFIG_YAML.yaml.getInt(ConfigEntries.SPAWN_DELAY.path);

                    // Warn the player about the delay.
                    String delayMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.INFO_TELEPORT_DELAY, false);
                    delayMessage = delayMessage.replace("%delay%", delay + "");
                    MessageUtils.sendColorizedMessage(commandSender, delayMessage);

                    // Put the player in the ArrayList of players waiting to be teleported.
                    PluginCache.teleporting.add(username);
                    // Start a timer.
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,() ->
                    {
                            if(!PluginCache.cancelRunnable.contains(username) && PluginCache.teleporting.contains(username))
                            {

                                // Run method to check if there are any args and teleport the player accordingly.
                                checkArgsAndTeleport(args, commandSender);

                            }
                            PluginCache.cancelRunnable.remove(username);

                    }, delay * 20); // Convert seconds to ticks.
                    return true;
                }
                // If it's already teleporting...
                else
                {
                    // Send a message to the player stating it, and do nothing.
                    MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.ERROR_ALREADY_TELEPORTING);
                    return true;
                }
            }
        }
        else
        {
            // Warn the player he doesn't have permissions to go to the spawn.
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_TELEPORT.permission);
            commandSender.sendMessage(errorMessage);
        }

        return true;
    }


    private void checkArgsAndTeleport(String[] args, CommandSender commandSender)
    {
        // Make a variable for the world name.
        String worldName;

        // Cast player to commandSender so we can get its position.
        Player player = (Player)commandSender;

        String username = commandSender.getName();

        // Check if there are any args.
        if(args.length > 0)
        {

                // Set world name as args[0].
                worldName = args[0];
        }
        else // If there are no args...
        {
            // Set world name as current world.
            worldName = player.getWorld().getName();
        }

        // Teleport the player to the spawn.
        teleportToSpawn(player, player, worldName);
    }


    // Method to teleport the player to the hub.
    public static void teleportToSpawn(CommandSender sender, Player player, String worldName)
    {
        String username = player.getName();

        // No need to check if the world is null: TeleportUtils will already handle that.
        debugger.sendDebugMessage(Level.INFO, "Player name: " + username + "; World name: " + worldName);
        // Teleport the player to the destination.
        TeleportUtils.teleportPlayer(sender, player, FileUtils.FileType.SPAWN_YAML, worldName);
        // Remove the player from the teleporting list, since it's not teleporting anymore.
        // Also remove it from the "teleporting" list - so it won't get teleported if it's waiting the hub delay.
        PluginCache.teleporting.remove(username);
    }
}
