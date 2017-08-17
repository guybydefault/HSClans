package ru.lexmint.hsclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.lexmint.hsclans.domain.CPLayer;

/**
 * Author: lexmint.
 * Created for: HSArena.
 * Date: 14.06.15 (19:31).
 * <p/>
 * This event
 */
public class PowerLossDeathEvent extends Event implements Cancellable {

    private CPLayer cpLayer;

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    public PowerLossDeathEvent(CPLayer cpLayer) {
        this.cpLayer = cpLayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return Player who has lost some power.
     */
    public CPLayer getCPLayer() {
        return cpLayer;
    }

    public Player getPlayer() {
        return cpLayer.getPlayer();
    }
}
