package wtf.beatrice.hubthat.utils;

import org.bukkit.command.CommandSender;

public class PermissionUtils
{

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
