package its_meow.claimitgui.network;

import io.netty.buffer.ByteBuf;
import its_meow.claimitgui.client.event.ClaimDeletionResultEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SClaimDeletionResultPacket implements IMessage {

    public DeletionResult result;

    public SClaimDeletionResultPacket() {}

    public SClaimDeletionResultPacket(DeletionResult result) {
        this.result = result;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int i = buf.readInt();
        if(i >= 0 && i < DeletionResult.values().length) {
            result = DeletionResult.values()[i];
        } else {
            result = DeletionResult.PACKET_ERROR;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(result.ordinal());
    }

    public static enum DeletionResult {
        NO_PERM,
        NO_EXIST,
        PACKET_ERROR,
        DELETED;
    }
    
    public static class Handler implements IMessageHandler<SClaimDeletionResultPacket, IMessage> {

        @Override
        public IMessage onMessage(SClaimDeletionResultPacket message, MessageContext ctx) {
            MinecraftForge.EVENT_BUS.post(new ClaimDeletionResultEvent(message.result));
            return null;
        }

    }

}