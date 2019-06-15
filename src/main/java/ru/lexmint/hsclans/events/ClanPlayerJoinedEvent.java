package ru.lexmint.hsclans.events;

import ru.lexmint.hsclans.domain.CPLayer;
import ru.lexmint.hsclans.domain.Clan;

public class ClanPlayerJoinedEvent extends HSClansEvent {

    private CPLayer cpLayer;
    private Clan clan;

    public ClanPlayerJoinedEvent(CPLayer cpLayer, Clan clan) {
        this.cpLayer = cpLayer;
        this.clan = clan;
    }
}
