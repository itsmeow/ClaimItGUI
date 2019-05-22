package its_meow.claimitgui.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SClaimRemovePacket implements IMessage {
    
    public String trueName;
    
    public SClaimRemovePacket() {}
    
    public SClaimRemovePacket(ClaimArea claim) {
        trueName = claim.getTrueViewName();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int l = buf.readInt();
        trueName = String.valueOf(buf.readCharSequence(l, Charsets.UTF_8));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(trueName.length());
        buf.writeCharSequence(trueName, Charsets.UTF_8);
    }
    
    public static class Handler implements IMessageHandler<SClaimRemovePacket, IMessage> {

        @Override
        public IMessage onMessage(SClaimRemovePacket message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT) {
                return null;
            }
            ClaimArea claim = ClaimManager.getManager().getClaimByTrueName(message.trueName);
            if(claim != null) {
                ClaimManager.getManager().deleteClaim(claim);
            }
            return null;
        }
        
    }
    
}
