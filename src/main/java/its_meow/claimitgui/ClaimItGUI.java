package its_meow.claimitgui;

import java.util.UUID;

import its_meow.claimit.AdminManager;
import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.event.claim.ClaimAddedEvent;
import its_meow.claimit.api.event.claim.ClaimRemovedEvent;
import its_meow.claimit.api.group.Group;
import its_meow.claimit.api.group.GroupManager;
import its_meow.claimitgui.network.CDeleteClaimPacket;
import its_meow.claimitgui.network.CRefreshListPacket;
import its_meow.claimitgui.network.SClaimAddPacket;
import its_meow.claimitgui.network.SClaimDeletionResultPacket;
import its_meow.claimitgui.network.SClaimRemovePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = ClaimItGUI.MOD_ID)
@Mod(modid = ClaimItGUI.MOD_ID, name = ClaimItGUI.NAME, version = ClaimItGUI.VERSION, acceptedMinecraftVersions = ClaimItGUI.acceptedMCV, dependencies = "after-required:claimitapi;after-required:claimit")
public class ClaimItGUI {

    public static final String MOD_ID = "claimitgui";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "ClaimIt GUI";
    public static final String acceptedMCV = ClaimItAPI.acceptedMCV;

    @Instance(ClaimItGUI.MOD_ID)
    public static ClaimItGUI mod;

    public static final SimpleNetworkWrapper NET = NetworkRegistry.INSTANCE.newSimpleChannel(ClaimItGUI.MOD_ID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        int packets = 0;
        NET.registerMessage(SClaimAddPacket.Handler.class, SClaimAddPacket.class, packets++, Side.CLIENT);
        NET.registerMessage(SClaimRemovePacket.Handler.class, SClaimRemovePacket.class, packets++, Side.CLIENT);
        NET.registerMessage(CRefreshListPacket.Handler.class, CRefreshListPacket.class, packets++, Side.SERVER);
        NET.registerMessage(CDeleteClaimPacket.Handler.class, CDeleteClaimPacket.class, packets++, Side.SERVER);
        NET.registerMessage(SClaimDeletionResultPacket.Handler.class, SClaimDeletionResultPacket.class, packets++, Side.CLIENT);
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {

    }

    @SubscribeEvent
    public static void playerLogin(PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            for(ClaimArea claim : ClaimManager.getManager().getClaimsList()) {
                if(shouldSendClaim(player, claim)) {
                    NET.sendTo(new SClaimAddPacket(claim), player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void claimAdd(ClaimAddedEvent event) {
        ClaimArea claim = event.getClaim();
        for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            if(shouldSendClaim(player, claim)) {
                NET.sendTo(new SClaimAddPacket(claim), player);
            }
        }
    }

    @SubscribeEvent
    public static void claimRemove(ClaimRemovedEvent event) {
        ClaimArea claim = event.getClaim();
        for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            NET.sendTo(new SClaimRemovePacket(claim), player);
        }
    }

    public static boolean shouldSendClaim(EntityPlayerMP player, ClaimArea claim) {
        UUID uuid = player.getGameProfile().getId();
        for(Group group : GroupManager.getGroupsForClaim(claim)) {
            if(group.getMembers().containsKey(uuid)) {
                return true;
            }
        }
        if(claim.isOwner(uuid) || AdminManager.isAdmin(player) || claim.getMembers().containsKey(uuid)) {
            return true;
        }
        return false;
    }

}
