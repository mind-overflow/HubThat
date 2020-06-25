package net.mindoverflow.hubthat.commands;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.utils.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

import static net.mindoverflow.hubthat.utils.TeleportUtils.fixInvisibilityAfter;
import static net.mindoverflow.hubthat.utils.TeleportUtils.fixInvisibilityBefore;

public class WorldTpCommand implements CommandExecutor
{

    // Initialize the debugger so I can debug the plugin.
    private final Debugger debugger = new Debugger(getClass().getName());


    // Initialize the plugin variable so we can access all of the plugin's data.
    private final HubThat plugin;

    // Constructor to actually give "plugin" a value.
    public WorldTpCommand(HubThat givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());


        // If the command comes from Console, stop it and give a warning.
        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }

        // Check if the player has permission to teleport to any world.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.TELEPORT_TO_WORLD))
        {
            // Check if there is the correct number of args.
            if(args.length != 1)
            {
                // Warn the player in case it's wrong.
                String wrongUsageMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_WRONG_USAGE, false).replace("%usage%", "/worldtp <world>");
                MessageUtils.sendColorizedMessage(commandSender, wrongUsageMessage);
                return true;
            }
            // Load the world's name from args and then the world.
            String destinationWorldName = args[0];
            World destinationWorld = plugin.getServer().getWorld(destinationWorldName);

            // If the world does not exist, warn the player.
            if(destinationWorld == null)
            {
                String worldDoesNotExistMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_WORLD_NOT_EXISTING, false);
                worldDoesNotExistMessage = worldDoesNotExistMessage.replace("%w%", destinationWorldName);
                MessageUtils.sendColorizedMessage(commandSender, worldDoesNotExistMessage);
                return true;
            }

            // Load the spawnpoint. This is going to be different from HubThat's spawnpoint because it could be in another world!
            Location destinationLocation = new Location(destinationWorld, destinationWorld.getSpawnLocation().getX(), destinationWorld.getSpawnLocation().getY(), destinationWorld.getSpawnLocation().getZ(), destinationWorld.getSpawnLocation().getYaw(), destinationWorld.getSpawnLocation().getPitch());
            // Cast Player to commandSender so we can teleport it.
            Player player = (Player)commandSender;
            // Teleport the Player.
            fixInvisibilityBefore(destinationLocation);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.teleport(destinationLocation), 1);
            fixInvisibilityAfter(player);
            // Tell the player he has been teleported.
            String teleportedMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.INFO_WORLDTP_TELEPORTED, false);
            teleportedMessage = teleportedMessage.replace("%world%", destinationWorldName).replace("%w%", destinationWorldName);
            MessageUtils.sendColorizedMessage(commandSender, teleportedMessage);
        }

        return true;
    }
}
