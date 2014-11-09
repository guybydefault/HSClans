package ru.lexmint;

import org.bukkit.plugin.java.JavaPlugin;
import ru.lexmint.cmd.CommandManager;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.StorageManager;
import ru.lexmint.utils.Config;
import ru.lexmint.utils.Debug;

/**
 * Core of the plugin.
 */
public class HSClans extends JavaPlugin {

    /**
     * Static instance of this plugin.
     */
    public static HSClans instance;

    /**
     * Debug object used to manage plugin's debugging process.
     */
    public Debug debug;

    /**
     * Config containing messages of the plugin.
     */
    public Config langConfig;

    /**
     * Config containing settings of the plugin.
     */
    public Config settings;

    /**
     * Class, which manages storage (interaction with mysql, etc)
     */
    public StorageManager storageManager;

    /**
     * Class, which manages clans (logic)
     */
    public ClanManager clanManager;

    @Override
    public void onEnable() {
        getLogger().info("***** ENABLING " + getDescription().getName() + " " + getDescription().getVersion() + " *****");
        long startingTime = System.currentTimeMillis();

        instance = this;

        /* Preparing debug */
        debug = new Debug(getDescription().getName());
        /* Preparing debug */


        /* Config initialization */
        getDataFolder().mkdirs();
        settings = new Config("config.yml");
        langConfig = new Config("lang.yml");
       /* Config initialization */


        /* Storage Managing */
        storageManager = new StorageManager();
        clanManager = new ClanManager(storageManager);


        /* Storage Managing */


        /* Registering command handling to CommandManager */
        for(String command : getDescription().getCommands().keySet()) {
            getCommand(command).setExecutor(new CommandManager());
        }
        /* Registering command handling to CommandManager */


        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been successfully enabled! (" + (System.currentTimeMillis() - startingTime) + "ms)");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been disabled!");
    }
}
