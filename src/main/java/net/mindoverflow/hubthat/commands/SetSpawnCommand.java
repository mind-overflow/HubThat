package net.mindoverflow.hubthat.commands;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.utils.*;
import net.mindoverflow.hubthat.utils.files.FileUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.logging.Level;

public class SetSpawnCommand implements CommandExecutor
{

    // Initialize the debugger so I can debug the plugin.
    private final Debugger debugger = new Debugger(getClass().getName());


    // Initialize the plugin variable so we can access all of the plugin's data.
    private final HubThat plugin;


    // Constructor to actually give "plugin" a value.
    public SetSpawnCommand(HubThat givenPlugin) { plugin = givenPlugin; }


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


        // Check if the player has permission to set the hub.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.SPAWN_SET))
        {

            // Cast player to commandSender so we can get its position.
            Player player = (Player)commandSender;
            // Load the player's position.
            Location playerLocation = player.getLocation();
            double x, y, z, yaw, pitch;
            String currentWorldName, worldYouAreSettingTheSpawnOf;
            x = playerLocation.getX();
            y = playerLocation.getY();
            z = playerLocation.getZ();
            yaw = playerLocation.getYaw();
            pitch = playerLocation.getPitch();
            // We need the world name to be non null.
            currentWorldName = Objects.requireNonNull(playerLocation.getWorld()).getName();

            // Check if there are any args and if they are different from the actual world.
            if(args.length > 0 && !args[0].equalsIgnoreCase(currentWorldName))
            {
                // We need to set the world you're setting the spawn of to args[0] so we can differentiate between it and it's new spawn destination.
                worldYouAreSettingTheSpawnOf = args[0];
            }
            // If there are no args, we are going to use the current player's world as the one with the new spawn.
            else
            {
                worldYouAreSettingTheSpawnOf = currentWorldName;
                Objects.requireNonNull(plugin.getServer().getWorld(worldYouAreSettingTheSpawnOf)).setSpawnLocation((int)x, (int)y, (int)z);
            }

            // If the world does not exist...
            World world = plugin.getServer().getWorld(worldYouAreSettingTheSpawnOf);
            if(world == null)
            {
                // Warn the player and stop.
                // Tell the player that the world does not exist.
                String errorWorldNotExistingMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.ERROR_WORLD_NOT_EXISTING, false);
                errorWorldNotExistingMessage = errorWorldNotExistingMessage.replace("%w%", worldYouAreSettingTheSpawnOf);
                MessageUtils.sendColorizedMessage(commandSender, errorWorldNotExistingMessage);
                return true;
            }

            // Set the location to the Yaml file.
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.world." + worldYouAreSettingTheSpawnOf, currentWorldName);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.x." + worldYouAreSettingTheSpawnOf, x);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.y." + worldYouAreSettingTheSpawnOf, y);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.z." + worldYouAreSettingTheSpawnOf, z);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.yaw." + worldYouAreSettingTheSpawnOf, yaw);
            FileUtils.FileType.SPAWN_YAML.yaml.set("spawn.pitch." + worldYouAreSettingTheSpawnOf, pitch);

            // Edit the vanilla world's spawnpoint. 
            //world.setSpawnLocation(new Location(world, x, y, z, (float)yaw, (float)pitch));

            // Save the file to the disk. We don't need to reload the Yaml file because we already set the values in the RAM.
            FileUtils.saveExistingYaml(FileUtils.FileType.SPAWN_YAML);

            // Tell the player he set the spawn successfully.
            String spawnSetMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.INFO_SPAWN_SET, false)
            .replace("%dw%", worldYouAreSettingTheSpawnOf)
            .replace("%cw%", currentWorldName)
            .replace("%x%", (int)x + "")
            .replace("%y%", (int)y + "")
            .replace("%z%", (int)z + "");

            MessageUtils.sendColorizedMessage(commandSender, spawnSetMessage);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.SPAWN_SET.permission);
            commandSender.sendMessage(errorMessage);
        }




        return true;
    }
}
