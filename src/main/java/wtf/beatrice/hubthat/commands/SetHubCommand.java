package wtf.beatrice.hubthat.commands;

import wtf.beatrice.hubthat.utils.*;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SetHubCommand implements CommandExecutor
{

    // Initialize the debugger so I can debug the plugin.
    private final Debugger debugger = new Debugger(getClass().getName());

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {

        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());


        // If the command comes from Console, stop it and give a warning.
        boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessage.ERROR_CONSOLE_ACCESS_BLOCKED);
            return true;
        }

        // Check if the player has permission to set the hub.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.HUB_SET))
        {
            // Cast player to commandSender so we can get its position.
            Player player = (Player)commandSender;
            // Load the player's position.
            Location playerLocation = player.getLocation();
            double x, y, z, yaw, pitch;
            String worldName;
            x = playerLocation.getX();
            y = playerLocation.getY();
            z = playerLocation.getZ();
            yaw = playerLocation.getYaw();
            pitch = playerLocation.getPitch();
            worldName = playerLocation.getWorld().getName();

            // Set the location to the Yaml file.
            FileUtils.FileType.HUB_YAML.yaml.set("hub.x", x);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.y", y);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.z", z);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.yaw", yaw);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.pitch", pitch);
            FileUtils.FileType.HUB_YAML.yaml.set("hub.world", worldName);

            // Save the file to the disk. We don't need to reload the Yaml file because we already set the values in the RAM.
            FileUtils.saveExistingYaml(FileUtils.FileType.HUB_YAML);

            // Tell the player he set the hub successfully.
            String hubSetMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.INFO_HUB_SET, false);
            hubSetMessage = hubSetMessage.replace("%w%", worldName);
            hubSetMessage = hubSetMessage.replace("%x%", (int)x + "");
            hubSetMessage = hubSetMessage.replace("%y%", (int)y + "");
            hubSetMessage = hubSetMessage.replace("%z%", (int)z + "");
            MessageUtils.sendColorizedMessage(commandSender, hubSetMessage);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessage.NO_PERMISSION, true).replace("%permission%", Permissions.HUB_SET.permission);
            commandSender.sendMessage(errorMessage);
        }
        return true;
    }
}
