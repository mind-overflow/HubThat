package wtf.beatrice.hubthat.listeners;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.commands.HubCommand;
import wtf.beatrice.hubthat.commands.SpawnCommand;
import wtf.beatrice.hubthat.utils.ConfigEntry;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class PlayerRespawnListener implements Listener
{

    private final HubThat plugin;
    public PlayerRespawnListener(HubThat givenPlugin)
    {
        plugin = givenPlugin;
    }



    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        YamlConfiguration configYaml = FileUtils.FileType.CONFIG_YAML.yaml;



        if(configYaml.getBoolean(ConfigEntry.MULTIVERSE_BYPASS.path))
        {
            plugin.getServer().getScheduler().runTaskLater(plugin, ()->  tpPlayer(event.getPlayer(), configYaml), 5L);

        }
        else
        {

            tpPlayer(event.getPlayer(), configYaml);
        }


    }

    private void tpPlayer(Player player, YamlConfiguration configYaml)
    {
        // Check if the respawn handler is enabled in config.
        if(configYaml.getBoolean(ConfigEntry.TELEPORTATION_RESPAWN_HANDLER.path))
        {

            // Check if the player has to be teleported to Hub on respawn.
            if(configYaml.getBoolean(ConfigEntry.TELEPORTATION_TP_HUB_ON_RESPAWN.path))
            {
                HubCommand.teleportToHub(player, player);
            }
            else
            {
                // If it's disabled, it means the player has to go to his dying world's spawn.
                SpawnCommand.teleportToSpawn(player, player, player.getWorld().getName());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        /*final Player player = event.getEntity();
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            Class<?>PacketPlayInClientCommand = null;
            try {

                Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer");
                Object craftPlayer = craftPlayerClass.cast(player);
                Method handle = craftPlayer.getClass().getMethod("getHandle", new Class[0]);
                Object entityPlayer = handle.invoke(craftPlayer, new Class[0]);
                Field playerConnection = entityPlayer.getClass().getField("playerConnection");



                //PacketPlayInClientCommand = Class.forName("net.minecraft.server.v1_15_R1.PacketPlayInClientCommand");

                if (player.isDead())
                {
                    ((PlayerConnection)playerConnection).a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

        });*/
    }
}
