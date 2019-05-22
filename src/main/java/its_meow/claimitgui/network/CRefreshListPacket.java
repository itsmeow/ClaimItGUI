package its_meow.claimitgui.network;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimitgui.ClaimItGUI;
import its_meow.claimitgui.client.ClientClaimManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CRefreshListPacket implements IMessage {

    public HashSet<String> claimNames = new HashSet<String>();

    public CRefreshListPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        int len = buf.readInt();
        for(int i = 0; i < len; i++) {
            int sLen = buf.readInt();
            String name = String.valueOf(buf.readCharSequence(sLen, Charsets.UTF_8));
            claimNames.add(name);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int size = ClientClaimManager.getClaimsList().size();
        buf.writeInt(size);
        for(ClaimArea claim : ClientClaimManager.getClaimsList()) {
            buf.writeInt(claim.getTrueViewName().length());
            buf.writeCharSequence(claim.getTrueViewName(), Charsets.UTF_8);
        }
    }

    public static class Handler implements IMessageHandler<CRefreshListPacket, IMessage> {

        @Override
        public IMessage onMessage(CRefreshListPacket message, MessageContext ctx) {
            if(ctx.side != Side.SERVER) {
                return null;
            }
            Set<String> names = message.claimNames;
            EntityPlayerMP player = (EntityPlayerMP) ctx.getServerHandler().player;
            for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
                if(!names.contains(claim.getTrueViewName()) && ClaimItGUI.shouldSendClaim(player, claim)) {
                    ClaimItGUI.NET.sendTo(new SClaimAddPacket(claim), player);
                } else if(names.contains(claim.getTrueViewName()) && !ClaimItGUI.shouldSendClaim(player, claim)) {
                    ClaimItGUI.NET.sendTo(new SClaimRemovePacket(claim), player);
                }
            }
            return null;
        }

    }

}
