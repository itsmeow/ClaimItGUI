package its_meow.claimitgui.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class MainGUI extends GuiScreen {

    public static final int CLAIM_LIST_ID = 512325;
    public GuiButton claimListButton = null;
    public OwnedClaimListGUI claimList = null;
    public boolean drawClaimList = false;

    public MainGUI() {
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(this.mc);
        claimListButton = new GuiButton(CLAIM_LIST_ID, 0, 0, 50, 20, "Claim List");
        this.addButton(claimListButton);
        claimList = new OwnedClaimListGUI(Minecraft.getMinecraft(), this.mc.displayWidth, this.mc.displayHeight, this.mc.displayHeight, 0, 1);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if(drawClaimList) {
            claimList.drawScreen(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == CLAIM_LIST_ID && button == claimListButton) {
            this.drawClaimList = !this.drawClaimList;
        }
    }

}