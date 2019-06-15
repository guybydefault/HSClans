package ru.lexmint.hsclans.events;

import ru.lexmint.hsclans.domain.Claim;
import ru.lexmint.hsclans.domain.Clan;

public class ClaimUnclaimedEvent extends HSClansEvent {

    private Clan clan;
    private Claim claim;

    public ClaimUnclaimedEvent(Clan clan, Claim claim) {
        this.clan = clan;
        this.claim = claim;
    }
}
