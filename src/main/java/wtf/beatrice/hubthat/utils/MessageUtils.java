package wtf.beatrice.hubthat.utils;


import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public class MessageUtils
{
    // Initialize the Debugger instance.
    private static final Debugger debugger = new Debugger(MessageUtils.class.getName());

    // Method to automatically load and send a localized message to the CommandSender.
    public static void sendLocalizedMessage(CommandSender sender, LocalizedMessage messageEnum)
    {
        // If we actually have a sender, send it the message and color it!
        if(sender != null) sender.sendMessage(getLocalizedMessage(messageEnum, true));
        // If the sender is null, report it to the debugger.
        else debugger.sendDebugMessage(Level.SEVERE, "Sender is null!");
    }


    // Method to send a colorized message to the CommandSender.
    public static void sendColorizedMessage(CommandSender sender, String message)
    {
        // If we actually have a sender, send it the message!
        if(sender != null) sender.sendMessage(colorize(message));
            // If the sender is null, report it to the debugger.
        else debugger.sendDebugMessage(Level.SEVERE, "Sender is null!");
    }

    public static String getLocalizedMessage(LocalizedMessage messageEnum, boolean applyColor)
    {

        /*
         Load the string from the enum.
         We are doing this because we don't want random strings to be passed to this method: we want it done
         this way and this way only, so we don't get any error as every entry added to the enum is manually
         checked before actually adding it.
         */
        String path = messageEnum.path;

        // Initialize the Lang file.
        YamlConfiguration langFile = FileUtils.FileType.LANG_YAML.yaml;

        // Initialize the message string and store the String from the lang file to it.
        String localizedMessage = langFile.getString(LocalizedMessage.INFO_PREFIX.path) + langFile.getString(path);

        // Check if the message is null and if we have to color the message or leave the symbols inside for further elaboration.
        if (localizedMessage != null)
        {
            if(applyColor)
            {
                // Replace the famous '&' and '§' symbols with a ChatColor so we can color the messages!
                localizedMessage = colorize(localizedMessage);
            }

        } else
        {
            // Report if the message is null.
            debugger.sendDebugMessage(Level.SEVERE, "String " + path + " is null!");
        }
        // Return the message, whether it's null or not (if it's null, it's the same as writing "return null").
        return localizedMessage;
    }

    // Colorize Strings!
    public static String colorize(String str)
    {
        /*
         Since the translateAlternateColors method works with only one char and overwrites the previous one,
         we are going to replace '&' with '§' and then run that method with all the '§'s.
         */
        str = str.replace('&', '§');
        ChatColor.translateAlternateColorCodes('§', str);
        return str;
    }
}