package its_meow.claimitgui.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimitgui.client.ClientClaimManager;
import its_meow.claimitgui.client.event.ClaimDeletionResultEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SClaimDeletionResultPacket implements IMessage {

    public DeletionResult result;
    public String serialName;

    public SClaimDeletionResultPacket() {}

    public SClaimDeletionResultPacket(String serialName, DeletionResult result) {
        this.result = result;
        this.serialName = serialName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int i = buf.readInt();
        if(i >= 0 && i < DeletionResult.values().length) {
            result = DeletionResult.values()[i];
        } else {
            result = DeletionResult.PACKET_ERROR;
        }
        int l = buf.readInt();
        serialName = String.valueOf(buf.readCharSequence(l, Charsets.UTF_8));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(result.ordinal());
        buf.writeInt(serialName.length());
        buf.writeCharSequence(serialName, Charsets.UTF_8);
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
            MinecraftForge.EVENT_BUS.post(new ClaimDeletionResultEvent(ClientClaimManager.getClaimBySerialName(message.serialName), message.result));
            return null;
        }

    }

}