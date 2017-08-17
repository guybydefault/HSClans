package ru.lexmint.hsclans.integration;

import com.wimbli.WorldBorder.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.lexmint.hsclans.HSClans;

/**
 * Deals with world border plugin.
 */
public class Border {
    private static WorldBorder worldBorder;

    public static void setup() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (plugin == null || !(plugin instanceof WorldBorder)) {
            worldBorder = null;
            HSClans.instance.getLogger().severe("WorldBorder not found! Integration has failed.");
        } else {
            worldBorder = (WorldBorder) plugin;
        }
    }

    public static WorldBorder getWorldBorder() {
        return worldBorder;
    }


}
