package its_meow.claimitgui.network;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.ClientClaimManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SClaimAddPacket extends ClaimPacket {
    
    public SClaimAddPacket() {}

    public SClaimAddPacket(ClaimArea claim) {
        super(claim);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int hash = buf.readInt();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        claim = ClaimArea.deserialize(tag, String.valueOf(hash));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(claim.hashCode());
        ByteBufUtils.writeTag(buf, claim.serialize());
    }

    public static class Handler implements IMessageHandler<SClaimAddPacket, IMessage> {

        @Override
        public IMessage onMessage(SClaimAddPacket message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT) {
                return null;
            }
            ClientClaimManager.addClaim(message.claim);
            return null;
        }

    }

}
