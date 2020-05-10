package net.mindoverflow.hubthat;

import net.mindoverflow.hubthat.commands.*;
import net.mindoverflow.hubthat.completers.InfoCompleter;
import net.mindoverflow.hubthat.completers.SpawnCompleter;
import net.mindoverflow.hubthat.listeners.PlayerChatListener;
import net.mindoverflow.hubthat.listeners.PlayerJoinListener;
import net.mindoverflow.hubthat.listeners.PlayerMoveListener;
import net.mindoverflow.hubthat.listeners.PlayerRespawnListener;
import net.mindoverflow.hubthat.utils.files.FileUtils;
import net.mindoverflow.hubthat.utils.files.OldConfigConversion;
import net.mindoverflow.hubthat.utils.statistics.Metrics;
import net.mindoverflow.hubthat.utils.statistics.UpdateChecker;
import net.mindoverflow.hubthat.utils.*;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HubThat extends JavaPlugin
{
    // Instantiate a Debugger for this class.
    private Debugger debugger = new Debugger(getClass().getName());

    // Initializing needed variables.
    public static Logger logger;
    private PluginManager pluginManager;
    public UpdateChecker updateChecker;

    // Method called when the plugin is being loaded.
    @Override
    public void onEnable()
    {

        // Give the initialized variables their respective values. Absolutely don't do this before as they will look good in the IDE but will result null.
        logger = getLogger();
        pluginManager = getServer().getPluginManager();

        // Check and report if the Debugger is enabled (the method itself does the checking). Would do it before but we need the logger to be initialized! :(
        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZING PLUGIN ]---");
        debugger.sendDebugMessage(Level.INFO, "Logger and PluginManager already initialized.");

        // Register instances and give them the plugin parameter (this, because this class is the JavaPlugin) so they can access all of its info.
        debugger.sendDebugMessage(Level.INFO, "Instantiating some classes that need to access to plugin data...");
        FileUtils fileUtilsInstance = new FileUtils(this);
        HubThatCommand hubThatCommandInstance = new HubThatCommand(this);
        HubCommand hubCommandInstance = new HubCommand(this);
        SetHubCommand setHubCommandInstance = new SetHubCommand(this);
        SpawnCommand spawnCommandInstance = new SpawnCommand(this);
        SetSpawnCommand setSpawnCommandInstance = new SetSpawnCommand(this);
        WorldListCommand worldListCommandInstance = new WorldListCommand(this);
        WorldTpCommand worldTpCommandInstance = new WorldTpCommand(this);
        UpdateChecker updateCheckerInstance = new UpdateChecker(this);

        // We need to instantiate Utils classes because they need to access plugin data and server.
        PermissionUtils permissionUtilsInstance = new PermissionUtils(this);
        TeleportUtils teleportUtilsInstance = new TeleportUtils(this);
        MessageUtils messageUtilsInstance = new MessageUtils(this);
        updateChecker = new UpdateChecker(this);
        debugger.sendDebugMessage(Level.INFO, "Done instantiating classes!");

        // Register Listeners
        debugger.sendDebugMessage(Level.INFO, "Registering listeners...");
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new PlayerRespawnListener(this), this);


        
        debugger.sendDebugMessage(Level.INFO, "Done registering listeners!");

        // Register Commands
        debugger.sendDebugMessage(Level.INFO, "Registering commands...");

        getCommand("hubthat").setExecutor(hubThatCommandInstance);
        getCommand("hubthat").setTabCompleter(new InfoCompleter());

        getCommand("hub").setExecutor(hubCommandInstance);

        getCommand("sethub").setExecutor(setHubCommandInstance);

        getCommand("spawn").setExecutor(spawnCommandInstance);
        getCommand("spawn").setTabCompleter(new SpawnCompleter());

        getCommand("setspawn").setExecutor(setSpawnCommandInstance);
        getCommand("setspawn").setTabCompleter(new SpawnCompleter());

        getCommand("worldlist").setExecutor(worldListCommandInstance);

        getCommand("worldtp").setExecutor(worldTpCommandInstance);
        getCommand("worldtp").setTabCompleter(new SpawnCompleter());

        debugger.sendDebugMessage(Level.INFO, "Done registering commands!");

        OldConfigConversion.checkOldConfig(this, logger);
        // Check if all needed files exist and work correctly, also loading their YAMLs.
        debugger.sendDebugMessage(Level.INFO, "Checking files...");
        FileUtils.checkFiles();
        debugger.sendDebugMessage(Level.INFO, "Done checking files!");

        /*
        Load all the YAML files. We are already loading them in FileUtils's checkFiles() method but we are loading them singularly.
        With this method we are sure that all the files get successfully loaded. Better twice than never...
         */
        debugger.sendDebugMessage(Level.INFO, "Reloading YAML config...");
        FileUtils.reloadYamls();
        debugger.sendDebugMessage(Level.INFO, "Done!");

        // Check for updates, if they are enabled.
        if(FileUtils.FileType.CONFIG_YAML.yaml.getBoolean(ConfigEntries.UPDATE_CHECKER_ENABLED.path))
        {
            debugger.sendDebugMessage(Level.INFO, "Update checking is enabled.");
            // Start the update checking delayed job. It will handle checking updates, storing variables and telling the console.
            debugger.sendDebugMessage(Level.INFO, "Running task (via Main).");
            //UpdateChecker.runTimer();
            // Check if the links are valid.
            /*debugger.sendDebugMessage(Level.INFO, "Checking if links are valid via Main.");
            if(updateCheckerInstance.linksValid())
            {
                debugger.sendDebugMessage(Level.INFO, "Links are valid.");
                // Check if the update is needed (if newest version is different from current version).
                // We need to surround it with try/catch because it may throw a IOException,
                try
                {
                    debugger.sendDebugMessage(Level.INFO, "Checking updates are needed via Main.");
                    updateCheckerInstance.checkUpdates();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            { // If the links are not valid... (eg: server/internet is offline)
                // Log it to the console.
                logger.log(Level.SEVERE, "There's a problem with the updates server.");
                logger.log(Level.SEVERE, "Please check if there are any updates manually.");
            }*/
        }

        debugger.sendDebugMessage(Level.INFO,"Setting up Metrics...");
        setupMetrics();
        debugger.sendDebugMessage(Level.INFO,"Done setting up Metrics!");

        // Send success output message to console.
        logger.log(Level.INFO, "Plugin " + getDescription().getName() + " Successfully Loaded!");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZATION DONE ]---");

    }

    // Method called when the plugin is being unloaded.
    @Override
    public void onDisable() {
        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ DISABLING PLUGIN ]---");
        getServer().getScheduler().cancelTasks(this);
        logger.log(Level.INFO, "Plugin " + getDescription().getName() + " Successfully Unloaded!");
        debugger.sendDebugMessage(Level.WARNING, "---[ PLUGIN DISABLED ]---");
    }

    private void setupMetrics()
    {

        Metrics metrics = new Metrics(this);

        YamlConfiguration config = FileUtils.FileType.CONFIG_YAML.yaml;
        metrics.addCustomChart(new Metrics.SimplePie("respawn-handler", () -> config.getString(ConfigEntries.TELEPORTATION_RESPAWN_HANDLER.path)));
        metrics.addCustomChart(new Metrics.SimplePie("world-related-chat", () -> config.getString(ConfigEntries.WORLD_RELATED_CHAT.path)));
        metrics.addCustomChart(new Metrics.SimplePie("update-notify", () -> config.getString(ConfigEntries.UPDATE_CHECKER_ENABLED.path)));
        metrics.addCustomChart(new Metrics.SimplePie("set-gamemode-on-join", () -> config.getString(ConfigEntries.GAMEMODE_SET_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("tp-hub-on-join", () -> config.getString(ConfigEntries.TELEPORTATION_TP_HUB_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("tp-hub-on-respawn", () -> config.getString(ConfigEntries.TELEPORTATION_TP_HUB_ON_RESPAWN.path)));

        if(config.getBoolean(ConfigEntries.GAMEMODE_SET_ON_JOIN.path))
        {
            metrics.addCustomChart(new Metrics.SimplePie("join-gamemode", () -> config.getString(ConfigEntries.GAMEMODE.path)));
        }
    }

}