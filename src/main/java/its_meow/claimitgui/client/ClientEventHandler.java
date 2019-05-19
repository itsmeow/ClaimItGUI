package its_meow.claimitgui.client;

import its_meow.claimitgui.ClaimItGUI;
import its_meow.claimitgui.client.gui.MainGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = ClaimItGUI.MOD_ID, value = Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onGuiInit(InitGuiEvent.Post event) {
        if(event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaledRes = new ScaledResolution(mc);
            int width = scaledRes.getScaledWidth();
            event.getButtonList().add(new GuiButton(5604342, (width / 2) - (width / 3), 0, 50, 20, "ClaimIt"));
        }
    }

    @SubscribeEvent
    public static void onGuiActionPerformed(ActionPerformedEvent event) {
        if((event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) && event.getButton().id == 5604342) {
            Minecraft.getMinecraft().displayGuiScreen(new MainGUI());
        }
    }

}