package its_meow.claimitgui;

import java.util.UUID;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.api.ClaimItAPI;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.api.event.claim.ClaimAddedEvent;
import its_meow.claimitgui.network.SClaimAddPacket;
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
            UUID uuid = player.getGameProfile().getId();
            ImmutableList<ClaimArea> claims = ClaimManager.getManager().getClaimsList();
            for(ClaimArea claim : claims) {
                if(claim.isOwner(uuid) || claim.canManage(player)) {
                    NET.sendTo(new SClaimAddPacket(claim), player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void claimCreate(ClaimAddedEvent event) {
        ClaimArea claim = event.getClaim();
        for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            UUID uuid = player.getGameProfile().getId();
            if(claim.isOwner(uuid) || claim.canManage(player)) {
                NET.sendTo(new SClaimAddPacket(claim), player);
            }
        }
    }

}
