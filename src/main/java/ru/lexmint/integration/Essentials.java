package ru.lexmint.integration;


import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import ru.lexmint.HSClans;

/**
 * Deals with essentials.
 */
public class Essentials {
    private static IEssentials essentials;

    public static void setup() {
        Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
        if (ess != null) {
            essentials = (IEssentials) ess;
            try {
                Chat.setup();
            } catch (NoClassDefFoundError error) {
                HSClans.instance.getLogger().info("EssentialsChat is on one of the latest versions. Integration is not needed.");
            }
        } else {
            HSClans.instance.getLogger().severe("Essentials has not been found! Integration is unavailable.");
        }
    }

    /**
     * Return false if features is disabled or Essentials is not available.
     *
     * @param command Command which has caused teleport.
     * @param player  Player who will be teleported.
     * @param loc     Location to which player is going to teleport.
     * @return True if teleport has been handled. Otherwise, if feature is disabled or Essentials is not available, false.
     */
    public static boolean handleTeleport(String command, Player player, Location loc) {
        if (!HSClans.instance.getSettings().getBoolean("integration.essentials") || essentials == null) {
            return false;
        }
        Teleport teleport = essentials.getUser(player).getTeleport();
        Trade trade = new Trade(command, essentials);
        try {
            teleport.teleport(loc, trade, PlayerTeleportEvent.TeleportCause.COMMAND);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }


}
