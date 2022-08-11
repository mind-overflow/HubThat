package wtf.beatrice.hubthat.commands;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.commands.hubthatcommands.HelpCommand;
import wtf.beatrice.hubthat.commands.hubthatcommands.ReloadCommand;
import wtf.beatrice.hubthat.utils.Debugger;
import wtf.beatrice.hubthat.utils.MessageUtils;
import wtf.beatrice.hubthat.utils.PluginCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class HubThatCommand implements CommandExecutor
{

    // Initialize the plugin variable so we can access all of the plugin's data.
    private final HubThat plugin;

    // Initialize the debugger so I can debug the plugin.
    private final Debugger debugger = new Debugger(getClass().getName());

    // Constructor to actually give "plugin" a value.
    public HubThatCommand(HubThat givenPlugin)
    {
        plugin = givenPlugin;
    }



    // Override the default command. Set the instructions for this particular command (registered in the main class).
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        // Log who is using the command.
        debugger.sendDebugMessage(Level.INFO, "Sender is instance of: " + commandSender.getClass().getName());

        // If the command comes from Console, give a warning.
        // We need no warning because this command is not player reliant.
        /*boolean senderIsConsole = (commandSender instanceof ConsoleCommandSender);
        if(senderIsConsole)
        {
            MessageUtils.sendLocalizedMessage(commandSender.getName(), LocalizedMessages.WARNING_CONSOLE_ACCESS);
        }*/

        // Check if there are any args.
        if(args.length == 0)
        {
            MessageUtils.sendColorizedMessage(commandSender, "&6" + plugin.getName() +"&7 version &6" + plugin.getDescription().getVersion() + "&7 for &6SpigotMC/CraftBukkit &6" + PluginCache.minSupportedVersion + "&7-&6" + PluginCache.maxSupportedVersion + "&7.");
            MessageUtils.sendColorizedMessage(commandSender, "&7Coded by &6" + debugger.authorName + "&7, &6GNU GPLv3&7).");
            commandSender.sendMessage("");
            MessageUtils.sendColorizedMessage(commandSender, "&7Write &6/"+ plugin.getName().toLowerCase() + " help&7 to see plugin commands.");
        }
        // Check if there is a single argument after the command itself.
        else if (args.length == 1)
        {
            if(args[0].equalsIgnoreCase("help"))
            {
                HelpCommand.infoCommand(commandSender, plugin);
            }
            // Check if the args are "reload" and in case, do it.
            else if(args[0].equalsIgnoreCase("reload"))
            {
                ReloadCommand.reloadCommand(commandSender, plugin);
            }
        }
        return true;
    }
}
