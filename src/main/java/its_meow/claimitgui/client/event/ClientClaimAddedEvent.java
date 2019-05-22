package its_meow.claimitgui.client.event;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.claim.ClaimEvent;

public class ClientClaimAddedEvent extends ClaimEvent {

    public ClientClaimAddedEvent(ClaimArea claim) {
        super(claim);
    }

}
