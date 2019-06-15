package ru.lexmint.hsclans.events;

import ru.lexmint.hsclans.domain.Claim;
import ru.lexmint.hsclans.domain.Clan;

public class ClaimCapturedEvent extends HSClansEvent {

    private Claim claim;
    /**
     * Can be null in case it was wilderness (claim not owned by any clan).
     */
    private Clan oldOwner;
    private Clan newOner;

    public ClaimCapturedEvent(Claim claim, Clan oldOwner, Clan newOner) {
        this.claim = claim;
        this.oldOwner = oldOwner;
        this.newOner = newOner;
    }
}
