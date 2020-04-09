package net.mindoverflow.hubthat.commands;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.utils.*;
import net.mindoverflow.hubthat.utils.files.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.logging.Level;

public class HubCommand  implements CommandExecutor
{


    // Initialize the debugger so I can debug the plugin.
    private static Debugger debugger = new Debugger(HubCommand.class.getName());

    // Initialize the plugin variable so we can access all of the plugin's data.
    private static HubThat plugin;

    // Constructor to actually give "plugin" a value.
    public HubCommand(HubThat givenPlugin) { plugin = givenPlugin; }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {

        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        // Store the commandSender name for easy access.
        String username = commandSender.getName();


        if(args.length > 0)
        {
            // Check if the player has permission to teleport to the hub.
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_TELEPORT_OTHERS))
            {
                String teleportingPlayerName = args[0];
                Player teleportingPlayer = plugin.getServer().getPlayer(teleportingPlayerName);
                if(teleportingPlayer == null)
                {
                    String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_PLAYER_OFFLINE, true).replace("%player%", teleportingPlayerName);
                    commandSender.sendMessage(errorMessage);
                    return true;
                }
                else
                {
                    teleportToHub(commandSender, teleportingPlayer);
                    return true;
                }
            }
            else
            {
                // Warn the player he doesn't have permissions to teleport others to the hub.
                String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_TELEPORT_OTHERS.permission);
                commandSender.sendMessage(errorMessage);
                return true;
            }
        }


        // If the command comes from Console, stop it and give a warning.
        boolean senderIsConsole = !(commandSender instanceof Player);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }


        // Check if the player has permission to teleport to the hub.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_TELEPORT))
        {

            // Check if the player has permission to skip the teleport delay.
            if(PermissionUtils.playerHasPermission(commandSender, Permissions.NO_HUB_DELAY))
            {

                teleportToHub(commandSender, (Player)commandSender);
                return true;
            }
            // If the player doesn't have permission to skip the teleport delay...
            else
            {
                // Check if he's not already teleporting.
                if(!CommonValues.teleporting.contains(username))
                {
                    // Put the player in the ArrayList of players waiting to be teleported.
                    CommonValues.teleporting.add(username);
                    // Load the teleportation delay.
                    int delay = FileUtils.FileType.CONFIG_YAML.yaml.getInt(ConfigEntries.HUB_DELAY.path);


                    // Warn the player about the delay.
                    String delayMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.INFO_TELEPORT_DELAY, false);
                    delayMessage = delayMessage.replace("%delay%", delay + "");
                    MessageUtils.sendColorizedMessage(commandSender, delayMessage);

                    // Start a timer.
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if(!CommonValues.cancelRunnable.contains(username) && CommonValues.teleporting.contains(username))
                            {
                                teleportToHub(commandSender, (Player)commandSender);
                            }
                            CommonValues.cancelRunnable.remove(username);

                        }
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
            // Warn the player he doesn't have permissions to go to the hub.
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_TELEPORT.permission);
            commandSender.sendMessage(errorMessage);
            return true;
        }
    }

    // Method to teleport the player to the hub.
    public static void teleportToHub(CommandSender sender, Player player)
    {
        String username = player.getName();

        // Require the world to be Non Null
        String worldName = Objects.requireNonNull(player.getWorld().getName());
        debugger.sendDebugMessage(Level.INFO, "Player name: " + username + "; World name: " + worldName);
        // Teleport the player to the destination.
        TeleportUtils.teleportPlayer(sender, player, FileUtils.FileType.HUB_YAML, worldName);
        // Remove it from the "teleporting" list - so it won't get teleported if it's waiting the spawn delay.
        CommonValues.teleporting.remove(username);
    }
}
