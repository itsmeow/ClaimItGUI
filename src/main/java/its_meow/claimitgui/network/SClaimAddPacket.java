package its_meow.claimitgui.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SClaimAddPacket implements IMessage {

    public ClaimArea claim;
    
    public SClaimAddPacket() {}
    
    public SClaimAddPacket(ClaimArea claim) {
        this.claim = claim;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int l = buf.readInt();
        String serialName = String.valueOf(buf.readCharSequence(l, Charsets.UTF_8));
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        claim = ClaimArea.deserialize(tag, serialName);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(claim.getSerialName().length());
        buf.writeCharSequence(claim.getSerialName(), Charsets.UTF_8);
        ByteBufUtils.writeTag(buf, claim.serialize());
    }
    
    public static class Handler implements IMessageHandler<SClaimAddPacket, IMessage> {

        @Override
        public IMessage onMessage(SClaimAddPacket message, MessageContext ctx) {
            if(ctx.side != Side.CLIENT) {
                return null;
            }
            ClaimManager.getManager().addClaim(message.claim);
            return null;
        }
        
    }

}
