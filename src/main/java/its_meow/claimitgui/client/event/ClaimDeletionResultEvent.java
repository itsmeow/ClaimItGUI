package its_meow.claimitgui.client.event;

import its_meow.claimitgui.network.SClaimDeletionResultPacket.DeletionResult;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClaimDeletionResultEvent extends Event {
    
    public final DeletionResult result;
    
    public ClaimDeletionResultEvent(DeletionResult result) {
        this.result = result;
    }
    
}
