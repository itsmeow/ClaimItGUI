package its_meow.claimitgui.network;

import io.netty.buffer.ByteBuf;
import its_meow.claimitgui.client.ClientClaimManager;
import its_meow.claimitgui.client.event.ClaimDeletionResultEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SClaimDeletionResultPacket implements IMessage {

    public DeletionResult result;
    public int hash;

    public SClaimDeletionResultPacket() {}

    public SClaimDeletionResultPacket(int hash, DeletionResult result) {
        this.result = result;
        this.hash = hash;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int i = buf.readInt();
        if(i >= 0 && i < DeletionResult.values().length) {
            result = DeletionResult.values()[i];
        } else {
            result = DeletionResult.PACKET_ERROR;
        }
        hash = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(result.ordinal());
        buf.writeInt(hash);
    }

    public static enum DeletionResult {
        NO_PERM,
        NO_EXIST,
        PACKET_ERROR,
        DELETED;
    }

    public static class Handler implements INullResponseHandler<SClaimDeletionResultPacket> {

        @Override
        public void req(SClaimDeletionResultPacket message, MessageContext ctx) {
            MinecraftForge.EVENT_BUS.post(new ClaimDeletionResultEvent(ClientClaimManager.getClaimByHash(message.hash), message.result));
        }

    }

}