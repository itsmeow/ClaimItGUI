package its_meow.claimitgui.client.gui.objects;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraft.client.Minecraft;

public class GuiScrollClaimPanel extends GuiScrollPanel<ClaimArea> {

    public GuiScrollClaimPanel(int width, int height, int topIn, int bottomIn) {
        super(width, height, topIn, bottomIn, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2);
    }

    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks, ClaimArea claim) {
        mc.fontRenderer.drawString(String.valueOf(slotIndex  + 1), 10, yPos, 0x6b6b6b);
        mc.fontRenderer.drawString(claim.getDisplayedViewName(), 35, yPos, 0xe4e4e4);
    }

}
