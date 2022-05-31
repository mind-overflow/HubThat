package wtf.beatrice.hubthat.commands.hubthatcommands;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.utils.*;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ReloadCommand
{
    private static final Debugger debugger = new Debugger(ReloadCommand.class.getName());


    public static void reloadCommand(CommandSender commandSender, HubThat plugin)
    {
        if(PermissionUtils.playerHasPermission(commandSender, Permissions.RELOAD_CONFIG))
        {
            debugger.sendDebugMessage(Level.INFO, "Reloading YAMLS...");
            MessageUtils.sendColorizedMessage(commandSender, "&7Reloading &e" + plugin.getName() + "&7 v&e" + plugin.getDescription().getVersion() + "&7...");
            FileUtils.checkFiles();
            FileUtils.reloadYamls();
            MessageUtils.sendColorizedMessage(commandSender, "&eReloaded!");
            debugger.sendDebugMessage(Level.INFO, "Reloaded YAMLs!");

            // This method checks if player has permissions, checks error codes, and acts accordingly.
            plugin.updateChecker.playerMessage(commandSender);
        }
        else
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.RELOAD_CONFIG.permission);
            commandSender.sendMessage(errorMessage);
        }


    }
}
