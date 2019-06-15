package ru.lexmint.hsclans.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;


public class ClanPlayerLeftEvent extends HSClansEvent {

    private CPLayer cpLayer;
    private Clan clan;

    public ClanPlayerLeftEvent(CPLayer cpLayer, Clan clan) {
        this.cpLayer = cpLayer;
        this.clan = clan;
    }
}
