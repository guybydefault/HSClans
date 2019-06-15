package ru.lexmint.hsclans;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import ru.lexmint.hsclans.cmd.CommandManager;
import ru.lexmint.hsclans.domain.ClanManager;
import ru.lexmint.hsclans.domain.StorageManager;
import ru.lexmint.hsclans.domain.io.MySQL;
import ru.lexmint.hsclans.integration.Border;
import ru.lexmint.hsclans.integration.ClansPlaceholderExpansion;
import ru.lexmint.hsclans.integration.Essentials;
import ru.lexmint.hsclans.integration.WorldGuardIntegration;
import ru.lexmint.hsclans.listener.*;
import ru.lexmint.hsclans.utils.AutoLeaveTask;
import ru.lexmint.hsclans.utils.ClanMessenger;
import ru.lexmint.hsclans.utils.ScoreboardManager;
import ru.lexmint.hscore.utils.Config;
import ru.lexmint.hscore.utils.Debug;

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
    private ClanMessenger clanMessenger;

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

    /**
     * Class which manages with commands.
     */
    private CommandManager commandManager;

    private ScoreboardManager scoreboardManager;

    /**
     * Listener which logs info, updates player's power and something important stats.
     */
    private MonitorListener monitorListener;
    /**
     * Listens to chat, manages with formatting.
     */
    private ChatListener chatListener;
    /**
     * Fixes some annoying bugs and glitches.
     */
    private ExploitListener exploitListener;

    /**
     * Deals with player respawn and other things connected with player.
     */
    private PlayerListener playerListener;

    /**
     * Protects clans' claims from block destroying/placing and etc.
     */
    private BlockListener blockListener;

    /**
     * Deals with entities (enderman, for example) and PvP damage.
     */
    private EntityListener entityListener;


    @Override
    public void onEnable() {
        long startingTime = System.currentTimeMillis();

        instance = this;

        /* Preparing debug and messaging */
        debug = new Debug(getDescription().getName(), this);
        /* Preparing debug and messaging */

        /* Config initialization */
        getDataFolder().mkdirs();
        settings = new Config("config.yml", this, debug);
        langConfig = new Config("lang.yml", this, debug);
       /* Config initialization */

        /* Messaging */
        clanMessenger = new ClanMessenger(langConfig, debug);
        /* Messaging */

        /* Storage Managing */
        storageManager = new StorageManager();
        clanManager = new ClanManager(storageManager);
        clanManager.loadData();
        /* Storage Managing */


        /* Registering command handling to CommandManager */
        commandManager = new CommandManager();
        for (String command : getDescription().getCommands().keySet()) {
            getCommand(command).setExecutor(commandManager);
        }
        /* Registering command handling to CommandManager */

        scoreboardManager = new ScoreboardManager();
        scoreboardManager.onLoad();

        /* Registering listeners */
        monitorListener = new MonitorListener();
        chatListener = new ChatListener();
        exploitListener = new ExploitListener();
        playerListener = new PlayerListener();
        blockListener = new BlockListener();
        entityListener = new EntityListener();

        getServer().getPluginManager().registerEvents(monitorListener, this);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(exploitListener, this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(blockListener, this);
        getServer().getPluginManager().registerEvents(entityListener, this);
        /* Registering listeners */

        /* We need to make sure that all online players are loaded to cache and the count down
        of their play time on server (for statistics) has been started.
         */
        for (Player player : Bukkit.getOnlinePlayers()) {
            monitorListener.onPlayerJoin(player);
        }

        /* Auto-Leave from clan task for inactive players */
        if (getSettings().getBoolean("player.auto-leave.enabled")) {
            int autoLeavePeriod = (int) (getSettings().getDouble("player.auto-leave.period") * 60 * 60 * 20);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), 0, autoLeavePeriod);
        }

        /* Essentials integration */
        Essentials.setup();
        WorldGuardIntegration.setup();
        Border.setup();
        ClansPlaceholderExpansion.setup();

        getLogger().info("" + getDescription().getName() + " " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " ENABLED! (" + (System.currentTimeMillis() - startingTime) + "MS)");
    }

    @Override
    public void onDisable() {
         /* We need to make sure that all online players are unloaded from cache and the count down
        of their play time on server (for statistics) has been finished.
         */
        for (Player player : getServer().getOnlinePlayers()) {
            monitorListener.onPlayerLeave(player);
        }

        getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        MySQL.instance.disconnect();
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been disabled!");
    }

    /**
     * Gets clan manager which operates with clan logic.
     *
     * @return ClanManager object.
     */
    public ClanManager getClanManager() {
        return clanManager;
    }

    /**
     * Gets settings config.
     *
     * @return Config.
     */
    public Config getSettings() {
        return settings;
    }

    /**
     * Returns plugin's language (localisation) config.
     *
     * @return Config.
     */
    public Config getLangConfig() {
        return langConfig;
    }

    /**
     * Gets debug object which manages plugin's log file.
     *
     * @return Debug object.
     */
    public Debug getDebug() {
        return debug;
    }

    /**
     * Returns Messenger object.
     *
     * @return Messenger object, manages with all plugin's broadcasting, chatting and other stuff.
     */
    public ClanMessenger getMessenger() {
        return clanMessenger;
    }

    /**
     * @return Object which manages with commands.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public MonitorListener getMonitorListener() {
        return monitorListener;
    }

    public EntityListener getEntityListener() {
        return entityListener;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

}
