package its_meow.claimitgui.network;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.util.command.CommandUtils;
import its_meow.claimitgui.network.SClaimDeletionResultPacket.DeletionResult;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CDeleteClaimPacket extends ClaimPacket {
    
    public int hash;

    public CDeleteClaimPacket() {}

    public CDeleteClaimPacket(ClaimArea claim) {
        super(claim);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int hash = buf.readInt();
        claim = null;
        for(ClaimArea claim2 : ClaimManager.getManager().getClaimsList()) {
            if(claim2.hashCode() == hash) {
                claim = claim2;
                break;
            }
        }
        this.hash = hash;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(claim.hashCode());
    }

    public static class Handler implements IMessageHandler<CDeleteClaimPacket, SClaimDeletionResultPacket> {

        @Override
        public SClaimDeletionResultPacket onMessage(CDeleteClaimPacket message, MessageContext ctx) {
            try {
                if(message.claim == null) {
                    return new SClaimDeletionResultPacket(message.hash, DeletionResult.NO_EXIST);
                } else if(CommandUtils.isAdminWithNodeOrOwner(ctx.getServerHandler().player, message.claim, "claimit.command.claimit.claim.delete.others")) {
                    ClaimManager.getManager().deleteClaim(message.claim);
                    return new SClaimDeletionResultPacket(message.hash, DeletionResult.DELETED);
                } else {
                    return new SClaimDeletionResultPacket(message.hash, DeletionResult.NO_PERM);
                }
            } catch(Exception e) {
                return new SClaimDeletionResultPacket(message.hash, DeletionResult.PACKET_ERROR);
            }
        }

    }

}
