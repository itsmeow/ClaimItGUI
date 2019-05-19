package its_meow.claimitgui.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public class OwnedClaimListGUI extends GuiListExtended {

    public OwnedClaimListGUI(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }
    
    public static ClaimListEntry entry = new ClaimListEntry();
    
    @Override
    public IGuiListEntry getListEntry(int index) {
        return new ClaimListEntry();
    }

    @Override
    protected int getSize() {
        return 10;
    }
    
    public static class ClaimListEntry implements IGuiListEntry {

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
            
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            Minecraft.getMinecraft().fontRenderer.drawString("x " + x, x + 100, y + 100, 0);
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            
        }
        
    }

}
