package wtf.beatrice.hubthat.utils;

import wtf.beatrice.hubthat.HubThat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

public class Debugger
{
    // Enable or disable debugging messages (aka verbosity).
    private final boolean DEBUGGING = false;

    // Initialize needed variables. We will need those variables to be able to precisely debug the plugin.
    private String className;
    private String packageName;

    // Save my UUID and current Username somewhere so I can get debug messages too.
    public UUID authorUUID = UUID.fromString("297a1dc8-c0a3-485a-ad21-8956c749f927");
    public String authorName = "mind_overflow";

    // Make a constructor requiring to be given a class so we exactly know which class has made an instance of it and all of its properties.
    public Debugger(String instanceClassName)
    {
        // Only run this code and actually make a whole instance of the class only if debugging is active.
        if(DEBUGGING)
        {
            // Initializing the class variable and set it to this one: in case something bad happens, we still have the log without the class info.
            Class instanceClass = getClass();
            try
            {
                /*
                 * Try finding the instancing class. This is normally bad for performance as we have to search for the class since we only have its name
                 * but the only other way would have been to always instantiate a whole class instead of a single String, making the plugin resource
                 * hungry even if the Debugger was disabled.
                 */
                instanceClass = Class.forName(instanceClassName);
            }
            catch (ClassNotFoundException e)
            {
                // In case it throws an error, go mad and report it in console.
                HubThat.logger.log(Level.INFO, "WTF? A class made an instance of the Debugger but it somehow can't define which class was it. Very weird. Setting it to the Debugger class.");
                HubThat.logger.log(Level.INFO, "Please send the following error log to me (" + authorName + "):");
                e.printStackTrace();
            }
            // Give the instance's variables their respective values.
            className = instanceClass.getSimpleName();
            packageName = instanceClass.getPackage().getName();
        }

    }


    /*
     * Check if debugging is enabled and eventually send debug logs. No need to worry about some of this data being null as there already are
     * checks and fixed for that in the constructor. Also, the debugger must be instanced for this method to be called (it's not static), so
     * we already have the info we need thanks to that.
     */
    public void sendDebugMessage(Level lvl, String str)
    {

        // Check if debugging is enabled.
        if(DEBUGGING)
        {

            // Put together all the info we have in a single String.
            String msg = className + ": " + str;

            // Send the info to the server log.
            HubThat.logger.log(lvl, msg);

            // Check if I'm online and if I am, send me the same info.
            Player author = Bukkit.getPlayer(authorUUID);
            if(author == null) {
                author = Bukkit.getPlayer(authorName);
            }
            if(author != null)
            {
                if(Bukkit.getServer().getOnlinePlayers().contains(author))
                {
                    author.sendMessage(msg);
                }
            }
        }
    }
}
