package its_meow.claimitgui.client.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class MainGUI extends GuiScreen {

    public static final int CLAIM_LIST_ID = 512325;
    public GuiButton claimListButton = null;
    public ClaimListGUI claimList = null;

    public MainGUI() {
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void initGui() {
        claimListButton = new GuiButton(CLAIM_LIST_ID, 0, 0, 50, 20, "Claim List");
        this.addButton(claimListButton);
        claimList = new ClaimListGUI();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.id == CLAIM_LIST_ID && button == claimListButton) {
            Minecraft.getMinecraft().displayGuiScreen(claimList);
        }
    }

}