package its_meow.claimitgui.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface INullResponseHandler<REQ extends IMessage> extends IMessageHandler<REQ, IMessage> {
    
    default IMessage onMessage(REQ message, MessageContext ctx) {
        req(message, ctx);
        return null;
    }
    
    void req(REQ message, MessageContext ctx);
    
}
