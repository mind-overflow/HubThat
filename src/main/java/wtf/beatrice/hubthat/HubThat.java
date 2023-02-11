package wtf.beatrice.hubthat;

import wtf.beatrice.hubthat.commands.*;
import wtf.beatrice.hubthat.completers.InfoCompleter;
import wtf.beatrice.hubthat.completers.SpawnCompleter;
import wtf.beatrice.hubthat.listeners.PlayerChatListener;
import wtf.beatrice.hubthat.listeners.PlayerJoinListener;
import wtf.beatrice.hubthat.listeners.PlayerMoveListener;
import wtf.beatrice.hubthat.listeners.PlayerRespawnListener;
import wtf.beatrice.hubthat.utils.ConfigEntries;
import wtf.beatrice.hubthat.utils.Debugger;
import wtf.beatrice.hubthat.utils.TeleportUtils;
import wtf.beatrice.hubthat.utils.files.FileUtils;
import wtf.beatrice.hubthat.utils.files.OldConfigConversion;
import wtf.beatrice.hubthat.utils.metrics.Metrics;
import wtf.beatrice.hubthat.utils.metrics.UpdateChecker;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HubThat extends JavaPlugin
{
    // Instantiate a Debugger for this class.
    private final Debugger debugger = new Debugger(getClass().getName());

    // Initializing needed variables.
    public static Logger logger;
    private PluginManager pluginManager;
    public UpdateChecker updateChecker;
    private static HubThat instance;

    public HubThat()
    {
        instance = this;
    }

    // Method called when the plugin is being loaded.
    @Override
    public void onEnable()
    {

        // Initialize the variables with their respective values.
        // Don't do this in the constructor as they will result null.
        logger = getLogger();
        pluginManager = getServer().getPluginManager();

        // If debugging is enabled, this will be printed.
        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZING PLUGIN ]---");
        debugger.sendDebugMessage(Level.INFO, "Logger and PluginManager initialized.");

        // Initialize command classes.
        debugger.sendDebugMessage(Level.INFO, "Loading classes...");
        FileUtils fileUtilsInstance = new FileUtils(this);
        HubThatCommand hubThatCommandInstance = new HubThatCommand(this);
        HubCommand hubCommandInstance = new HubCommand(this);
        SpawnCommand spawnCommandInstance = new SpawnCommand(this);
        SetSpawnCommand setSpawnCommandInstance = new SetSpawnCommand(this);
        WorldListCommand worldListCommandInstance = new WorldListCommand(this);
        WorldTpCommand worldTpCommandInstance = new WorldTpCommand(this);

        // We need to instantiate Utils classes because they need to access the plugin instance.
        // todo: this is bad, utils should not be instantiated.
        TeleportUtils teleportUtilsInstance = new TeleportUtils(this);
        updateChecker = new UpdateChecker(this);
        debugger.sendDebugMessage(Level.INFO, "Classes loaded!");

        // Register Listeners
        debugger.sendDebugMessage(Level.INFO, "Registering listeners...");
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerMoveListener(), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new PlayerRespawnListener(this), this);


        debugger.sendDebugMessage(Level.INFO, "Listeners registered!");

        // Register Commands
        debugger.sendDebugMessage(Level.INFO, "Registering commands...");

        getCommand("hubthat").setExecutor(hubThatCommandInstance);
        getCommand("hubthat").setTabCompleter(new InfoCompleter());

        getCommand("hub").setExecutor(hubCommandInstance);

        getCommand("sethub").setExecutor(new SetHubCommand());

        getCommand("spawn").setExecutor(spawnCommandInstance);
        getCommand("spawn").setTabCompleter(new SpawnCompleter());

        getCommand("setspawn").setExecutor(setSpawnCommandInstance);
        getCommand("setspawn").setTabCompleter(new SpawnCompleter());

        getCommand("worldlist").setExecutor(worldListCommandInstance);

        getCommand("worldtp").setExecutor(worldTpCommandInstance);
        getCommand("worldtp").setTabCompleter(new SpawnCompleter());

        debugger.sendDebugMessage(Level.INFO, "Commands registered!");

        // Check if all needed files exist and work correctly, also loading their YAMLs.
        debugger.sendDebugMessage(Level.INFO, "Checking files...");
        OldConfigConversion.checkOldConfig(this, logger);
        FileUtils.checkFiles();
        debugger.sendDebugMessage(Level.INFO, "Done checking files!");

        /*
        Load all the YAML files. We are already loading them in FileUtils's checkFiles() method but we are loading them singularly.
        With this method we are sure that all the files get successfully loaded. Better twice than never...
         */
        debugger.sendDebugMessage(Level.INFO, "Loading configuration...");
        FileUtils.reloadYamls();
        debugger.sendDebugMessage(Level.INFO, "Configuration loaded!");

        debugger.sendDebugMessage(Level.INFO, "Setting up Metrics...");
        setupMetrics();
        debugger.sendDebugMessage(Level.INFO, "Done setting up Metrics!");

        // Send success output message to console.
        logger.log(Level.INFO, getDescription().getName() + " successfully loaded!");
        debugger.sendDebugMessage(Level.WARNING, "---[ INITIALIZATION DONE ]---");

    }

    // Method called when the plugin is being unloaded.
    @Override
    public void onDisable()
    {
        debugger.sendDebugMessage(Level.WARNING, "---[ DEBUGGER IS ENABLED! ]---");
        debugger.sendDebugMessage(Level.WARNING, "---[ DISABLING PLUGIN ]---");
        getServer().getScheduler().cancelTasks(this);
        logger.log(Level.INFO, getDescription().getName() + " unloaded!");
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
        metrics.addCustomChart(new Metrics.SimplePie("multiverse-bypass", () -> config.getString(ConfigEntries.MULTIVERSE_BYPASS.path)));
        metrics.addCustomChart(new Metrics.SimplePie("send-tp-message-on-join", () -> config.getString(ConfigEntries.TELEPORTATION_TP_MESSAGE_ON_JOIN.path)));
        metrics.addCustomChart(new Metrics.SimplePie("fix-invisible-after-tp", () -> config.getString(ConfigEntries.INVISIBILITY_FIX.path)));

        if (config.getBoolean(ConfigEntries.GAMEMODE_SET_ON_JOIN.path))
        {
            metrics.addCustomChart(new Metrics.SimplePie("join-gamemode", () -> config.getString(ConfigEntries.GAMEMODE.path)));
        }
    }

    public static HubThat getInstance()
    {
        return instance;
    }
}