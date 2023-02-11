package wtf.beatrice.hubthat.utils.files;

import wtf.beatrice.hubthat.HubThat;
import wtf.beatrice.hubthat.utils.ConfigEntries;
import wtf.beatrice.hubthat.utils.LocalizedMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class OldConfigConversion
{

    public static void checkOldConfig(HubThat plugin, Logger logger)
    {


        logger.warning(plugin.getName() + ": Checking if config exists...");
        File oldConfigFile = FileUtils.FileType.CONFIG_YAML.file;
        if(!oldConfigFile.exists()) return;


        logger.warning("Loading config...");
        YamlConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
        if(!oldConfig.getKeys(true).contains("global.VERSION")) return;


        logger.warning("WARNING! Old Configuration Detected!");
        logger.warning("Starting conversion now!");
        String configDir = plugin.getDataFolder().getAbsolutePath();

        logger.warning("Renaming old config...");
        oldConfigFile.renameTo(new File( configDir + File.separator + "config.old"));

        logger.warning("generating new files...");
        FileUtils.checkFiles();

        // newPath, oldPath
        HashMap<ConfigEntries, String>newAndOldConfigEntries = new HashMap<>();
        logger.warning("Loading config entries...");
        newAndOldConfigEntries.put(ConfigEntries.HUB_DELAY, "hub.delay");
        newAndOldConfigEntries.put(ConfigEntries.SPAWN_DELAY, "spawn.delay");
        newAndOldConfigEntries.put(ConfigEntries.WORLD_RELATED_CHAT, "global.world-related-chat");
        newAndOldConfigEntries.put(ConfigEntries.UPDATE_CHECKER_ENABLED, "updates.update-notify");
        newAndOldConfigEntries.put(ConfigEntries.MOVEMENT_DETECTION_ENABLED, "global.move-detect");
        newAndOldConfigEntries.put(ConfigEntries.GAMEMODE_SET_ON_JOIN, "global.set-gamemode-on-join");
        newAndOldConfigEntries.put(ConfigEntries.GAMEMODE, "global.gamemode");
        newAndOldConfigEntries.put(ConfigEntries.TELEPORTATION_TP_HUB_ON_JOIN, "global.tp-hub-on-join");
        newAndOldConfigEntries.put(ConfigEntries.TELEPORTATION_TP_HUB_ON_RESPAWN, "global.tp-hub-on-respawn");
        newAndOldConfigEntries.put(ConfigEntries.TELEPORTATION_RESPAWN_HANDLER, "global.respawn-handler");

        logger.warning("Converting config entries...");
        for(ConfigEntries entry : newAndOldConfigEntries.keySet())
        {
            logger.warning("Entry: " + entry.path);
            FileUtils.FileType.CONFIG_YAML.yaml.set(entry.path, oldConfig.get(newAndOldConfigEntries.get(entry)));
        }

        logger.warning("Saving file...");
        FileUtils.saveExistingYaml(FileUtils.FileType.CONFIG_YAML);
        logger.warning("Done with config.yml!");

        logger.warning("Loading lang entries...");
        HashMap<LocalizedMessage, String>newAndOldLangEntries = new HashMap<>();
        newAndOldLangEntries.put(LocalizedMessage.ERROR_ALREADY_TELEPORTING, "global.ALREADY-TELEPORTING");
        newAndOldLangEntries.put(LocalizedMessage.ERROR_HUB_NOT_SET, "hub.HUB_NOT_SET");
        newAndOldLangEntries.put(LocalizedMessage.ERROR_SPAWN_NOT_SET, "spawn.SPAWN_NOT_SET");
        newAndOldLangEntries.put(LocalizedMessage.ERROR_WORLD_NOT_EXISTING, "worldtp.UNKNOWN_WORLD");
        newAndOldLangEntries.put(LocalizedMessage.WARNING_TELEPORTATION_CANCELLED, "global.MOVED");
        newAndOldLangEntries.put(LocalizedMessage.INFO_HUB_TELEPORTED, "hub.TELEPORTED");
        newAndOldLangEntries.put(LocalizedMessage.INFO_SPAWN_TELEPORTED, "spawn.TELEPORTED");
        newAndOldLangEntries.put(LocalizedMessage.INFO_WORLDTP_TELEPORTED, "worldtp.TELEPORTED");

        logger.warning("Converting lang entries...");
        for(LocalizedMessage message : newAndOldLangEntries.keySet())
        {
            logger.warning("Entry: " + message.path);
            FileUtils.FileType.LANG_YAML.yaml.set(message.path, oldConfig.get(newAndOldLangEntries.get(message)));
        }


        logger.warning("Saving file...");
        FileUtils.saveExistingYaml(FileUtils.FileType.LANG_YAML);
        logger.warning("Done with lang.yml!");


    }

}
