package its_meow.claimitgui.client.gui;

import java.io.IOException;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.gui.objects.GuiScrollClaimPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ClaimListGUI extends GuiScreen {
    
    public GuiScrollClaimPanel panel;
    public int panelWidth = 10;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        this.panel.drawScreen(mouseX, mouseY, partialTicks);
        this.drawString(mc.fontRenderer, "Claims List", 10, 10, 0xeeee00);
        ClaimArea claim = panel.getSelectedItem();
        if(claim != null) {
            drawRect(panelWidth + 3, 25, width - 3, height - 3, 0xff727272);
            drawRect(panelWidth + 3, 25, width - 3, (height / 4), 0xff828282);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

    }

    @Override
    public void initGui() {
        panelWidth = (int) (this.width / 3F);
        panel = new GuiScrollClaimPanel(panelWidth, this.height, 25, this.height);
        for(int i = 0; i < 50; i++) {
            panel.addItem(randClaim());
        }
    }
    
    private static ClaimArea randClaim() {
        return new ClaimArea((int) (Math.random() * 10), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), UUID.randomUUID());
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        panel.handleMouseInput();
    }

}
