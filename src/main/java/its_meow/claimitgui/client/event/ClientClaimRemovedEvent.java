package its_meow.claimitgui.client.event;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.claim.ClaimEvent;

public class ClientClaimRemovedEvent extends ClaimEvent {

    public ClientClaimRemovedEvent(ClaimArea claim) {
        super(claim);
    }

}
