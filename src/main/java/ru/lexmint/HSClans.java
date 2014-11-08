package ru.lexmint;

import org.bukkit.plugin.java.JavaPlugin;
import ru.lexmint.cmd.CommandManager;
import ru.lexmint.domain.Clan;

/**
 * Core of the plugin.
 */
public class HSClans extends JavaPlugin {

    /**
     * Static instance of this plugin.
     */
    public static HSClans instance;

    @Override
    public void onEnable() {
        getLogger().info("***** ENABLING " + getDescription().getName() + " " + getDescription().getVersion() + " *****");
        long startingTime = System.currentTimeMillis();

        instance = this;

        /* Config initialization */
        this.getDataFolder().mkdirs();
        this.getConfig().options().copyDefaults(true);
        saveConfig();
       /* Config initialization */


        for(String command : getDescription().getCommands().keySet()) {
            getCommand(command).setExecutor(new CommandManager());
        }

        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been successfully enabled! (" + (System.currentTimeMillis() - startingTime) + "ms)");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " has been disabled!");
    }
}
