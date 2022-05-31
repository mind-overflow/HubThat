package wtf.beatrice.hubthat.listeners;

import wtf.beatrice.hubthat.utils.PluginCache;
import wtf.beatrice.hubthat.utils.ConfigEntries;
import wtf.beatrice.hubthat.utils.LocalizedMessages;
import wtf.beatrice.hubthat.utils.MessageUtils;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener
{

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {

        // Check if the movement detection is enabled.
        if(FileUtils.FileType.CONFIG_YAML.yaml.getBoolean(ConfigEntries.MOVEMENT_DETECTION_ENABLED.path))
        {
            // We are only going to allocate the playerName string and not the whole Player because we want efficiency.
            String playerName = event.getPlayer().getName();

            // Check if the player is waiting the teleport delay.
            if (PluginCache.teleporting.contains(playerName))
            {
                // Check if the player moved a whole block.
                if(event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                        event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                        event.getFrom().getBlockZ() != event.getTo().getBlockZ())
                {
                    // Remove the player from the list and warn him.
                    PluginCache.teleporting.remove(playerName);
                    PluginCache.cancelRunnable.add(playerName);
                    MessageUtils.sendLocalizedMessage(event.getPlayer(), LocalizedMessages.WARNING_TELEPORTATION_CANCELLED);
                }
            }
        }
    }
}