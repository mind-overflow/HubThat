package wtf.beatrice.hubthat.listeners;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.utils.ConfigEntry;
import wtf.beatrice.hubthat.utils.Debugger;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener
{

    // Instantiate a Debugger for this class.
    private final Debugger debugger = new Debugger(getClass().getName());

    private final HubThat plugin;
    public PlayerChatListener(HubThat givenPlugin)
    {
        plugin = givenPlugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // This is only related to players chatting - we don't need to check if console is running any command.
        Player messageSender = event.getPlayer();

        // Check if the world-related chat is enabled.
        if(FileUtils.FileType.CONFIG_YAML.yaml.getBoolean(ConfigEntry.WORLD_RELATED_CHAT.path))
        {

            // Store the sender's world spawn name in a string. Fallback to "__UNSET__".
            String senderWorldSpawn = FileUtils.FileType.SPAWN_YAML.yaml.getString("spawn.world." + messageSender.getWorld().getName(), "__UNSET__");

            // Iterate through each player connected to the server.
            for(Player messageReceiver : plugin.getServer().getOnlinePlayers())
            {
                // Store the receiver's world spawn name in a string. Fallback to "__UNSET__".
                String receiverWorldSpawn = FileUtils.FileType.SPAWN_YAML.yaml.getString("spawn.world." + messageReceiver.getWorld().getName(), "__UNSET__");

                // Check if the two world names match - and remove the receiver if they don't.
                if(!senderWorldSpawn.equalsIgnoreCase(receiverWorldSpawn))
                {
                    event.getRecipients().remove(messageReceiver);
                }
            }
        }
    }
}
