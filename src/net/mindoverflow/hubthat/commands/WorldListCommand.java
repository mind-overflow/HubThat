package net.mindoverflow.hubthat.commands;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.utils.*;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class WorldListCommand implements CommandExecutor
{


    // Initialize the debugger so I can debug the plugin.
    private final Debugger debugger = new Debugger(getClass().getName());


    // Initialize the plugin variable so we can access all of the plugin's data.
    private final HubThat plugin;

    // Constructor to actually give "plugin" a value.
    public WorldListCommand(HubThat givenPlugin) { plugin = givenPlugin; }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());


        // Check if player has permission to list worlds.
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.WORLD_LIST))
        {
            // send the translated title.
            MessageUtils.sendLocalizedMessage(commandSender, LocalizedMessages.INFO_WORLDS_LIST);
            MessageUtils.sendColorizedMessage(commandSender, "&7---------");


            // Iterate through all loaded worlds.
            int i = 0;
            for(World currentWorld : plugin.getServer().getWorlds())
            {
                i++;
                // Store world type and difficulty.
                String worldType = currentWorld.getWorldType().getName().toLowerCase();
                String worldDifficulty = currentWorld.getDifficulty().name().toLowerCase();
                World.Environment environment = currentWorld.getEnvironment();
                String worldEnvironment = environment.name().toLowerCase();
                if(environment == World.Environment.NETHER) worldEnvironment = "&c" + worldEnvironment;
                else if(environment == World.Environment.THE_END) worldEnvironment = "&d" + worldEnvironment;
                else if(environment == World.Environment.NORMAL) worldEnvironment = "&a" + worldEnvironment;

                // Store player numbers. We have a list of all players, so we will need to iterate through all of them.
                int playersNumber = 0;
                for(Player p : currentWorld.getPlayers())
                {
                    playersNumber++;
                }

                // Send the completed message.
                MessageUtils.sendColorizedMessage(commandSender, "&3" + i + "&7: &b" + currentWorld.getName() +
                        "&7, type: &e" + worldType +
                        "&7, players: &e" + playersNumber +
                        "&7, difficulty: &e" + worldDifficulty +
                        "&7, environment: &e" + worldEnvironment);
            }

            MessageUtils.sendColorizedMessage(commandSender, "&7---------");
        }
        else // If player doesn't have permissions...
        {
            // Tell him.
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.WORLD_LIST.permission);
            commandSender.sendMessage(errorMessage);
        }
        return true;
    }
}
