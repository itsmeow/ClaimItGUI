package its_meow.claimitgui.network;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ClaimPacket implements IMessage {

    public ClaimArea claim;

    public ClaimPacket() {}

    public ClaimPacket(ClaimArea claim) {
        this.claim = claim;
    }

}
