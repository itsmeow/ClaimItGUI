package its_meow.claimitgui.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimitgui.network.SClaimDeletionResultPacket.DeletionResult;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CDeleteClaimPacket extends ClaimPacket {
    
    public String serialName;

    public CDeleteClaimPacket() {}

    public CDeleteClaimPacket(ClaimArea claim) {
        super(claim);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int l = buf.readInt();
        String tName = String.valueOf(buf.readCharSequence(l, Charsets.UTF_8));
        claim = null;
        for(ClaimArea claim2 : ClaimManager.getManager().getClaimsList()) {
            if(claim2.getSerialName().equals(tName)) {
                claim = claim2;
                break;
            }
        }
        serialName = tName;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(claim.getSerialName().length());
        buf.writeCharSequence(claim.getSerialName(), Charsets.UTF_8);
    }

    public static class Handler implements IMessageHandler<CDeleteClaimPacket, SClaimDeletionResultPacket> {

        @Override
        public SClaimDeletionResultPacket onMessage(CDeleteClaimPacket message, MessageContext ctx) {
            try {
                if(message.claim == null) {
                    return new SClaimDeletionResultPacket(message.serialName, DeletionResult.NO_EXIST);
                } else if(CommandUtils.isAdminWithNodeOrOwner(ctx.getServerHandler().player, message.claim, "claimit.command.claimit.claim.delete.others")) {
                    ClaimManager.getManager().deleteClaim(message.claim);
                    return new SClaimDeletionResultPacket(message.serialName, DeletionResult.DELETED);
                } else {
                    return new SClaimDeletionResultPacket(message.serialName, DeletionResult.NO_PERM);
                }
            } catch(Exception e) {
                return new SClaimDeletionResultPacket(message.serialName, DeletionResult.PACKET_ERROR);
            }
        }

    }

}
