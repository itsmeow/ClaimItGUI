package its_meow.claimitgui.client.event;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.claim.ClaimEvent;

public class ClientClaimCreatedEvent extends ClaimEvent {

    public ClientClaimCreatedEvent(ClaimArea claim) {
        super(claim);
    }

}
