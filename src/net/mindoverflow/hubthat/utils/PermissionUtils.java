package net.mindoverflow.hubthat.utils;

import net.mindoverflow.hubthat.HubThat;
import org.bukkit.command.CommandSender;

public class PermissionUtils
{

    // Initialize the Debugger instance.
    private static Debugger debugger = new Debugger(PermissionUtils.class.getName());


    private static HubThat plugin;
    public PermissionUtils(HubThat givenPlugin)  { plugin = givenPlugin;  }

    // Method to get the permission string from the Permissions enum.
    public static boolean playerHasPermission(CommandSender user, Permissions permission)
    {
            if (user != null && user.hasPermission(permission.permission))
            {
                return true;
            }

        return false;
    }
}
