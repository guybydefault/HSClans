package ru.lexmint;

import org.bukkit.plugin.java.JavaPlugin;
import ru.lexmint.cmd.CommandManager;
import ru.lexmint.domain.ClanManager;
import ru.lexmint.domain.StorageManager;
import ru.lexmint.domain.io.MySQL;
import ru.lexmint.utils.Config;
import ru.lexmint.utils.Debug;
import ru.lexmint.utils.Messenger;

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
    private Debug debug;

    /**
     * Object which manages with all plugin's messages (broadcasting and other stuff).
     */
    private Messenger messenger;

    /**
     * Config containing messages of the plugin.
     */
    private Config langConfig;

    /**
     * Config containing settings of the plugin.
     */
    private Config settings;

    /**
     * Class, which manages storage (interaction with mysql, etc)
     */
    private StorageManager storageManager;

    /**
     * Class, which manages clans (logic)
     */
    private ClanManager clanManager;

    @Override
    public void onEnable() {
        getLogger().info("***** ENABLING " + getDescription().getName() + " " + getDescription().getVersion() + " *****");
        long startingTime = System.currentTimeMillis();

        instance = this;

        /* Preparing debug and messaging */
        debug = new Debug(getDescription().getName());
        messenger = new Messenger();
        /* Preparing debug and messaging */

        /* Config initialization */
        getDataFolder().mkdirs();
        settings = new Config("config.yml");
        langConfig = new Config("lang.yml");
       /* Config initialization */


        /* Storage Managing */
        storageManager = new StorageManager();
        clanManager = new ClanManager(storageManager);
        clanManager.loadData();
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
        MySQL.instance.disconnect();
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been disabled!");
    }

    /**
     * Gets storage manager which works with MySQL.
     * @return StorageManager object.
     */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * Gets clan manager which operates with clan logic.
     * @return ClanManager object.
     */
    public ClanManager getClanManager() {
        return clanManager;
    }

    /**
     * Gets settings config.
     * @return Config.
     */
    public Config getSettings() {
        return settings;
    }

    /**
     * Returns plugin's language (localisation) config.
     * @return Config.
     */
    public Config getLangConfig() {
        return langConfig;
    }

    /**
     * Gets debug object which manages plugin's log file.
     * @return Debug object.
     */
    public Debug getDebug() {
        return debug;
    }

    /**
     * Returns Messenger object.
     * @return Messenger object, manages with all plugin's broadcasting, chatting and other stuff.
     */
    public Messenger getMessenger() {
        return messenger;
    }

}
