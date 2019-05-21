package its_meow.claimitgui.client.gui;

import java.io.IOException;
import java.util.UUID;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.gui.objects.GuiScrollClaimPanel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ClaimListGUI extends GuiScreen {
    
    
    public static final int TITLE_BOX_COLOR = 0xff727272;
    public static final int MAIN_BOX_COLOR = 0xff828282;
    public static final int BACKGROUND_COLOR = 0xff424242;
    public static final int BACKGROUND_COLOR_2 = 0xff121212;
    public static final int MAIN_BACKGROUND_COLOR = 0xff626262;
    public static final int MAIN_BACKGROUND_COLOR_2 = 0xff424242;
    public GuiScrollClaimPanel panel;
    public int panelWidth = 10;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(0, 0, width, height, MAIN_BACKGROUND_COLOR, MAIN_BACKGROUND_COLOR_2);
        this.panel.drawScreen(mouseX, mouseY, partialTicks);
        drawVerticalLine(panelWidth, -1, height, 0xff2d2d2d);
        drawVerticalLine(panelWidth + 1, -1, height, 0xff2d2d2d);
        this.drawString(mc.fontRenderer, "Claims List", 10, 10, 0xeeee00);
        ClaimArea claim = panel.getSelectedItem();
        int xOff = 5;
        int yOff = 3;
        if(claim != null) {
            drawRect(panelWidth + xOff, yOff, width - xOff, height - yOff, TITLE_BOX_COLOR);
            drawRect(panelWidth + xOff, yOff, width - xOff, height / 4, MAIN_BOX_COLOR);
            drawHorizontalLine(panelWidth + xOff, width - xOff, yOff, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, width - xOff, height / 4, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, width - xOff, height - yOff, 0xff141414);
            drawVerticalLine(panelWidth + xOff, yOff, height - yOff, 0xff141414);
            drawVerticalLine(width - xOff, yOff, height - yOff, 0xff141414);
        } else {
            this.drawCenteredString(mc.fontRenderer, "No claim selected.", (width - (width / 3)), 15, 0xe3e3e3);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

    }

    @Override
    public void initGui() {
        panelWidth = (int) (this.width / 3F);
        panel = new GuiScrollClaimPanel(panelWidth, this.height, 25, this.height, BACKGROUND_COLOR, BACKGROUND_COLOR_2);
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
