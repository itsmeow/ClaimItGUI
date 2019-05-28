package its_meow.claimitgui.client.event;

import javax.annotation.Nullable;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.network.SClaimDeletionResultPacket.DeletionResult;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClaimDeletionResultEvent extends Event {
    
    public final DeletionResult result;
    @Nullable
    public final ClaimArea claim;
    
    public ClaimDeletionResultEvent(ClaimArea claim, DeletionResult result) {
        this.result = result;
        this.claim = claim;
    }
    
}
