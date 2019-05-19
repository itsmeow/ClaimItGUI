package its_meow.claimitgui;

import its_meow.claimit.api.ClaimItAPI;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod.EventBusSubscriber(modid = ClaimItGUI.MOD_ID)
@Mod(modid = ClaimItGUI.MOD_ID, name = ClaimItGUI.NAME, version = ClaimItGUI.VERSION, acceptedMinecraftVersions = ClaimItGUI.acceptedMCV, dependencies = "after-required:claimitapi;after-required:claimit")
public class ClaimItGUI {

    public static final String MOD_ID = "claimitgui";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "ClaimIt GUI";
    public static final String acceptedMCV = ClaimItAPI.acceptedMCV;

    @Instance(ClaimItGUI.MOD_ID)
    public static ClaimItGUI mod;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @EventHandler
    public void serverStop(FMLServerStoppingEvent event) {

    }

}
