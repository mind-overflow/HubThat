package net.mindoverflow.hubthat.commands.hubthatcommands;

import net.mindoverflow.hubthat.utils.*;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HelpCommand
{
    public static void infoCommand(CommandSender commandSender, Plugin plugin)
    {
        if(!PermissionUtils.playerHasPermission(commandSender, Permissions.HELP_MESSAGE))
        {
            String errorMessage = MessageUtils.getLocalizedMessage(LocalizedMessages.NO_PERMISSION, true).replace("%permission%", Permissions.HELP_MESSAGE.permission);
            commandSender.sendMessage(errorMessage);
            return;
        }

        MessageUtils.sendColorizedMessage(commandSender, "&8---------&0[&6HubThat Help Page&0]&8---------");
        MessageUtils.sendColorizedMessage(commandSender, "&6/hubthat&7: show HubThat Info");
        MessageUtils.sendColorizedMessage(commandSender, "&6/hubthat help&7: show this page - &8hubthat.help");
        MessageUtils.sendColorizedMessage(commandSender, "&6/hubthat reload&7: reload the config - &8hubthat.reloadconfig");
        MessageUtils.sendColorizedMessage(commandSender, "&6/hub&8 [player]&7: teleport to the Hub - &8hubthat.hub");
        MessageUtils.sendColorizedMessage(commandSender, "&6/spawn &8[world] [player]&7: teleport to current/another world's spawn - &8hubthat.spawn");
        MessageUtils.sendColorizedMessage(commandSender, "&6/sethub&7: set the server Hub - &8hubthat.sethub");
        MessageUtils.sendColorizedMessage(commandSender, "&6/setspawn &8[world]&7: set current/another world's Spawn - &8hubthat.setspawn");
        MessageUtils.sendColorizedMessage(commandSender, "&6/worldlist&7: list all the worlds - &8hubthat.listworlds");
        MessageUtils.sendColorizedMessage(commandSender, "&6/worldtp <world>&7: teleport to a world - &8hubthat.gotoworld");

    }
}
