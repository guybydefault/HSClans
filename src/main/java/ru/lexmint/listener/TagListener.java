package ru.lexmint.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;
import ru.lexmint.HSClans;
import ru.lexmint.domain.CPLayer;
import ru.lexmint.domain.ClanManager;

/**
 * Integration with TagAPI.
 */
public class TagListener implements Listener {
    /**
     * Integrated with TagAPI.
     * @param event Event fired when player is going to receive name tag.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTagChange(AsyncPlayerReceiveNameTagEvent event) {
        ClanManager clanManager = HSClans.instance.getClanManager();
        Player namedPlayer = event.getNamedPlayer();
        Player player = event.getPlayer();
        CPLayer cNamedPlayer = clanManager.getPlayer(namedPlayer.getName(), true);
        CPLayer cPlayer = clanManager.getPlayer(player.getName(), true);

        if (clanManager.areInTheSameClan(cPlayer, cNamedPlayer)) {
            event.setTag(ChatColor.GREEN + namedPlayer.getName());
        } else if (clanManager.areInAlliedClans(cPlayer, cNamedPlayer)) {
            event.setTag(ChatColor.LIGHT_PURPLE + namedPlayer.getName());
        } else {
            event.setTag(ChatColor.RED + namedPlayer.getName());
        }
    }
}
