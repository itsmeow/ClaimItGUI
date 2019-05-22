package its_meow.claimitgui.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.ClientClaimManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SClaimRemovePacket implements IMessage {
    
    public String serialName;
    
    public SClaimRemovePacket() {}
    
    public SClaimRemovePacket(ClaimArea claim) {
        serialName = claim.getSerialName();
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int l = buf.readInt();
        serialName = String.valueOf(buf.readCharSequence(l, Charsets.UTF_8));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(serialName.length());
        buf.writeCharSequence(serialName, Charsets.UTF_8);
    }
    
    public static class Handler implements IMessageHandler<SClaimRemovePacket, IMessage> {

        @Override
        public IMessage onMessage(SClaimRemovePacket message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT) {
                return null;
            }
            ClaimArea claim = ClientClaimManager.getClaimBySerialName(message.serialName);
            if(claim != null) {
                ClientClaimManager.deleteClaim(claim);
            }
            return null;
        }
        
    }
    
}
