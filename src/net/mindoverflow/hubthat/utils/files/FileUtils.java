package net.mindoverflow.hubthat.utils.files;

import net.mindoverflow.hubthat.HubThat;
import net.mindoverflow.hubthat.utils.CommonValues;
import net.mindoverflow.hubthat.utils.ConfigEntries;
import net.mindoverflow.hubthat.utils.Debugger;
import net.mindoverflow.hubthat.utils.statistics.UpdateChecker;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class FileUtils
{

    // Instantiate a Debugger for this class.
    private static Debugger debugger = new Debugger(FileUtils.class.getName());

    // Necessary variables.
    private static HubThat plugin;

    public FileUtils(HubThat plugin) {
        FileUtils.plugin = plugin;
    }

    public static void copyFileFromSrc(FileType givenFileType)
    {
        // Check if files already exists and if it doesn't, then create it.
        if(!givenFileType.file.exists())
        {
            // Load the InputStream of the file in the source folder.
            InputStream is = FileUtils.class.getResourceAsStream("/" + givenFileType.file.getName());
            try
            {
                // Try copying the file to the directory where it's supposed to be, and log it.
                Files.copy(is, Paths.get(givenFileType.file.getAbsolutePath()));
                is.close();
                debugger.sendDebugMessage(Level.INFO, "File " + givenFileType.file.getName() + " successfully created.");
            }
            catch (IOException e)
            {
                // Throw exception if something went wrong (lol, I expect this to happen since we're working with files in different systems)
                HubThat.logger.log(Level.SEVERE, "There were some unexpected errors from " + givenFileType.file.getName() + " file creation. Please contact the developer and send him this log:");
                e.printStackTrace();
            }

        }
    }

    // As method says, reload YamlConfigurations by overwriting their previous value.
    public static void reloadYamls()
    {
        for(FileType fileType : FileType.values())
        {
            fileType.yaml = YamlConfiguration.loadConfiguration(fileType.file);
        }

        YamlConfiguration config = FileType.CONFIG_YAML.yaml;

        if(config.getBoolean(ConfigEntries.UPDATE_CHECKER_ENABLED.path))
        {
            CommonValues.updateChecker = true;
            if(UpdateChecker.task != null)
            {
                plugin.getServer().getScheduler().cancelTask(UpdateChecker.task.getTaskId());
            }

            UpdateChecker.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, plugin.updateChecker, 1, 20 * 60 * 60 * 12);
        }

    }

    // Only reload the needed File.
    public static void reloadYaml(FileType yamlFile)
    {
        yamlFile.yaml = YamlConfiguration.loadConfiguration(yamlFile.file);
        debugger.sendDebugMessage(Level.INFO, "File " + yamlFile.file.getName() + " YAML loaded.");
    }

    // Save a Yaml file from the list of the plugin's YamlFiles enum.
    public static void saveExistingYaml(FileType yamlFile)
    {
        // Get the actual File and its location.
        File configFile = yamlFile.file;
        try {
            // Try saving the value in FileType.NAME.yaml into the file itself we just got. Else, it would only be saved in RAM and then be lost after unloading the plugin.
            yamlFile.yaml.save(configFile);
            debugger.sendDebugMessage(Level.INFO, "Successfully saved " + configFile.getName() +" (YAML)!");
        } catch (IOException e) {
            debugger.sendDebugMessage(Level.SEVERE, "Error in saving " + configFile.getName() + "(YAML)!");
            e.printStackTrace();
        }

        // Reload the Yaml configuration from the file, just in case.
        reloadYaml(yamlFile);
    }


    // Check if all needed files exist and work correctly.
    public static void checkFiles() {
        // Check if the different needed files and folders exist and if not, try creating them.
        // Check if plugin folder exists and eventually make it. Easy enough.
        if(!plugin.getDataFolder().exists())
        {
            if(plugin.getDataFolder().mkdir())
            {
                debugger.sendDebugMessage(Level.INFO, "Plugin dir successfully created.");
            }
        }

        for(FileType file : FileType.values())
        {

            // Check and eventually create config file.
            copyFileFromSrc(file);
            // Reload file YAML data into FileType.NAME.yaml.
            reloadYaml(file);
            // Check if there is any missing entry.
            checkYamlMissingEntries(file);
        }


        HubThat.logger.log(Level.INFO, "All files are working correctly.");
    }


    private static void checkYamlMissingEntries(FileType givenFile)
    {
        /*
         Load the file from source so we can check if the file in the plugin directory is missing any entries.
         Since our file is not an actual file on the filesystem but rather a compressed file in the jar archive,
         we can't directly access it via a "File file = new File();" method. To do it, we'd need to extract
         the file from the archive to a temporary file, read it and then delete it.

         The choice of making an InputStream instead is better because we don't risk leaving junk files
         in the filesystem and we can achieve the same objective without so many resource-consuming passages.
         */

        // First of all, we're gonna get the InputStream of the file from the jar archive.
        InputStream is = FileUtils.class.getResourceAsStream("/" + givenFile.file.getName());

        // Then, we're gonna make a Reader because we don't want to save it as a file but only load it in memory.
        // Bukkit's YamlConfiguration accepts Readers so this is perfect!
        Reader targetReader = new InputStreamReader(is);

        // Load its YamlConfiguration.
        YamlConfiguration srcYaml = YamlConfiguration.loadConfiguration(targetReader);

        // Iterate each entry in the YamlConfiguration.
        debugger.sendDebugMessage(Level.INFO, "Iterating src config entries for file " + givenFile.file.getName() + ".");

        /* For each String which we'll name 'key' in the Set of entries of the yaml file, do...

         getKeys(true) returns all the entries and all the sub-entries, which is what we need because
         we want to check the whole file for missing entries.
         If we wanted to only load an entry with the highest level sub-entries, we would just pass 'false'
         as an argument.

         Example
         ---- FILE ----------------
         hello: 'this is a string'
           myname: 4
           islorenzo: 8
             who: true
             areu: '?'
         john: false
         --------------------------

        Set<String> keys = srcYaml.getConfigurationSection("path").getKeys(true);

         By saving our set with 'false' as an argument, and "" as the path (which means the highest level of the file),
         we'd only get the 'hello' String and the 'john' boolean's value in the      set.

         By saving our set with 'false' as an argument, and "hello" as the path (which means the highest level of the
         'hello' entry), we'd only get the 'hello' String's value and the 'hello.myname' and 'hello.islorenzo' booleans' values in the set.

        By saving our set with 'true' as an argument, and "" as the path (which means the highest level of the file
        with all its sub-entries), we'd get the value of all entries in the whole file ('hello', 'hello.myname', 'hello.islorenzo',
        'hello.islorenzo.who', 'hello.islorenzo.areu', 'john') in the set.

         By saving our set with 'true' as an argument, and "hello" as the path (which means the highest level of the
         'hello' entry with all its sub-entries), we'd get the value of all entries in the 'hello' entry ('hello', 'hello.myname',
         'hello.islorenzo', 'hello.islorenzo.who', 'hello.islorenzo.areu') in the set.
         */
        for (String key : srcYaml.getConfigurationSection("").getKeys(true))
        {
            debugger.sendDebugMessage(Level.INFO, "Analyzing key '" + key + "' with default value '" + srcYaml.get(key) + "'.");

            // Check if file is missing every entry.
            if(!givenFile.yaml.contains(key))
            {
                debugger.sendDebugMessage(Level.WARNING, "Config file is missing '" + key + "' key! Proceeding to add it...");
                // Add the entry to the file.
                givenFile.yaml.set(key, srcYaml.get(key));
                debugger.sendDebugMessage(Level.WARNING, "Added key '" + key + "' with value '" + srcYaml.get(key) + "'.");
                // Save the file!
                saveExistingYaml(givenFile);
            }
        }
        debugger.sendDebugMessage(Level.INFO, " Done iterating src config entries for file " + givenFile.file.getName() + "!");
    }
    // Save all the info about our files location.
    /*
    Also initialize all files and their config, so we know where are the files when we need to save or reload them.
    this is better than loading the files in the single classes that use them as if we had to reload them, we'd
    need to set them again in each of the classes. Doing this instead allows us to have them all in one place.
     */
    public enum FileType
    {
        //PLUGIN_FOLDER(plugin.getDataFolder(), null),
        CONFIG_YAML(new File(plugin.getDataFolder()+File.separator + "config.yml"), new YamlConfiguration()),
        LANG_YAML(new File(plugin.getDataFolder()+File.separator + "lang.yml"), new YamlConfiguration()),
        SPAWN_YAML(new File(plugin.getDataFolder()+File.separator + "spawn.yml"), new YamlConfiguration()),
        HUB_YAML(new File(plugin.getDataFolder()+File.separator + "hub.yml"), new YamlConfiguration());

        // Constructor, so we can assign the value we set here ^ to our File.
        public File file;
        public YamlConfiguration yaml;
        FileType(File givenFile, YamlConfiguration yamlConfig)
        {
            file = givenFile;
            yaml = yamlConfig;
        }
    }
}
