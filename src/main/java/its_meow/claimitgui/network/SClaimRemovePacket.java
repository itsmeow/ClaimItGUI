package its_meow.claimitgui.network;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.ClientClaimManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SClaimRemovePacket implements IMessage {
    
    public int hash;
    
    public SClaimRemovePacket() {}
    
    public SClaimRemovePacket(ClaimArea claim) {
        hash = claim.hashCode();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
    }
    
    public static class Handler implements INullResponseHandler<SClaimRemovePacket> {

        @Override
        public void req(SClaimRemovePacket message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT) {
                return;
            }
            ClaimArea claim = ClientClaimManager.getClaimByHash(message.hash);
            if(claim != null) {
                ClientClaimManager.deleteClaim(claim);
            }
        }
        
    }
    
}
