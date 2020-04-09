package net.mindoverflow.hubthat.listeners;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.commands.HubCommand;
import net.mindoverflow.hubthat.utils.*;
import net.mindoverflow.hubthat.utils.files.FileUtils;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerJoinListener implements Listener
{
    // Instantiate a Debugger for this class.
    private Debugger debugger = new Debugger(getClass().getName());

    private HubThat plugin;
    public PlayerJoinListener(HubThat givenPlugin)
    {
        plugin = givenPlugin;
    }

    // Call EventHandler and start listening to joining players.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Initialize needed variables for performance improvements and to avoid continuous method calls.
        Player player = e.getPlayer();
        String playerName = player.getName();
        YamlConfiguration configYaml = FileUtils.FileType.CONFIG_YAML.yaml;
        debugger.sendDebugMessage(Level.INFO, "Join Listener Works!");

        // Check if the player is me, the developer.
        if (player.getUniqueId().equals(debugger.authorUUID) || playerName.equals(debugger.authorName))
        {
            debugger.sendDebugMessage(Level.INFO, "Joining player is the developer!");

            // Send me a message about the current HubThat version.
            MessageUtils.sendColorizedMessage(player, "&7This server is running &3HubThat&7 v.&3" + plugin.getDescription().getVersion());
        }

        // This method checks if player has permissions, checks error codes, and acts accordingly.
        plugin.updateChecker.playerMessage(player);

        // Check if gamemode has to be set on join.
        if(configYaml.getBoolean(ConfigEntries.GAMEMODE_SET_ON_JOIN.path))
        {
            // Load the gamemode int from config.
            int gamemodeInt = configYaml.getInt(ConfigEntries.GAMEMODE.path);
            GameMode gamemode;

            // Set the gamemode accordingly.
            switch (gamemodeInt)
            {
                case 1:
                    gamemode = GameMode.CREATIVE;
                    break;
                case 2:
                    gamemode = GameMode.ADVENTURE;
                    break;
                case 3:
                    gamemode = GameMode.SPECTATOR;
                    break;
                default:
                    gamemode = GameMode.SURVIVAL;
                    break;
            }

            if(configYaml.getBoolean(ConfigEntries.MULTIVERSE_BYPASS.path))
            {
                plugin.getServer().getScheduler().runTaskLater(plugin, ()-> player.setGameMode(gamemode), 10L);
            }
            else
            {
                player.setGameMode(gamemode);
            }
        }

        // Check if we have to teleport the player to the Hub on join.
        if(configYaml.getBoolean(ConfigEntries.TELEPORTATION_TP_HUB_ON_JOIN.path))
        {

            if(configYaml.getBoolean(ConfigEntries.MULTIVERSE_BYPASS.path))
            {
                plugin.getServer().getScheduler().runTaskLater(plugin, ()-> HubCommand.teleportToHub(player, player), 10L);
            }
            else
            {
                HubCommand.teleportToHub(player, player);
            }
        }
    }
}
