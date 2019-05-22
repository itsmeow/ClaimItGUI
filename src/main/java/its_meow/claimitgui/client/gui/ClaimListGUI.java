package its_meow.claimitgui.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.client.gui.objects.GuiScrollClaimPanel;
import its_meow.claimitgui.client.gui.objects.GuiTabSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class ClaimListGUI extends GuiScreen {
    
    // Colors
    public static final int TITLE_BOX_COLOR = 0x9f727272;
    public static final int MAIN_BOX_COLOR = 0x9f828282;
    public static final int BACKGROUND_COLOR = 0xff424242;
    public static final int BACKGROUND_COLOR_2 = 0xff121212;
    public static final int MAIN_BACKGROUND_COLOR = 0xff626262;
    public static final int MAIN_BACKGROUND_COLOR_2 = 0xff424242;
    
    // Panel
    public GuiScrollClaimPanel panel;
    public int panelWidth = 10;
    
    public GuiTabSelection tabs;
    
    // Buttons
    public static final int LOCATION_TAB_BUTTON_ID = 0;
    public static final int PERMISSION_TAB_BUTTON_ID = 1;
    
    // General
    private ClaimArea lastClaim = null;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.drawBackground(0);
        this.drawDefaultBackground();
        //drawGradientRect(0, 0, width, height, MAIN_BACKGROUND_COLOR, MAIN_BACKGROUND_COLOR_2);
        this.panel.drawScreen(mouseX, mouseY, partialTicks);
        //drawVerticalLine(panelWidth, -1, height, 0xff2d2d2d);
        //drawVerticalLine(panelWidth + 1, -1, height, 0xff2d2d2d);
        this.drawString(mc.fontRenderer, "Claims List", 10, 10, 0xeeee00);
        ClaimArea claim = panel.getSelectedItem();
        if(claim != null) {
            // draw background
            int xOff = 5;
            int yOff = 3;
            String title = "Claim: " + claim.getDisplayedViewName();
            int titleWidth = mc.fontRenderer.getStringWidth(title);
            int topBarBottom = (int) (yOff + 8F + (mc.fontRenderer.FONT_HEIGHT * 1.5F));
            int titleBarX = (int) (panelWidth + xOff + 8F + titleWidth * 1.5F);
            drawRect(panelWidth + xOff, yOff, width - xOff, height - yOff, TITLE_BOX_COLOR);
            drawRect(panelWidth + xOff, yOff, width - xOff, 2 * topBarBottom, MAIN_BOX_COLOR);
            drawHorizontalLine(panelWidth + xOff, width - xOff, yOff, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, width - xOff, 2 * topBarBottom, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, width - xOff, height - yOff, 0xff141414);
            drawVerticalLine(panelWidth + xOff, yOff, height - yOff, 0xff141414);
            drawVerticalLine(width - xOff, yOff, height - yOff, 0xff141414);
            
            drawRect(panelWidth + xOff + 1, yOff + 1, titleBarX, topBarBottom, 0xff626262);
            // draw foreground
            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                this.mc.fontRenderer.drawStringWithShadow(title, (panelWidth + xOff + 3) / 1.5F, yOff + 3, 0xffffff);
            }
            GlStateManager.popMatrix();
            drawHorizontalLine(panelWidth + xOff + 1, titleBarX, topBarBottom - 1, 0xff141414);
            drawVerticalLine(titleBarX, yOff, topBarBottom, 0xff141414);
            if(tabs == null) {
                tabs = new GuiTabSelection(mc, titleBarX + 2, width - xOff, yOff + 1);
                tabs.addButton(LOCATION_TAB_BUTTON_ID, "Location");
                tabs.addButton(PERMISSION_TAB_BUTTON_ID, "Permissions");
                tabs.addButton(2, "Groups");
            }
            if(claim != lastClaim) {
                tabs.setDimensions(titleBarX + 2, width - xOff, yOff + 1);
            }
            tabs.drawScreen(mouseX, mouseY, partialTicks);
            float scaleO = 0.8F;
            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(scaleO, scaleO, scaleO);
                String name = claim.getOwner().toString();
                if(claim.getOwner().equals(Minecraft.getMinecraft().player.getGameProfile().getId())) {
                    name = Minecraft.getMinecraft().player.getName();
                }
                this.mc.fontRenderer.drawStringWithShadow("Owner: " + name, (panelWidth + xOff + 3) / scaleO, (topBarBottom + 3) / scaleO, 0xffffff);
            }
            GlStateManager.popMatrix();
        } else {
            this.drawCenteredString(mc.fontRenderer, "No claim selected.", (width - (width / 3)), 15, 0xe3e3e3);
        }
        lastClaim = claim;
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
        return new ClaimArea((int) (Math.random() * 10), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), Minecraft.getMinecraft().player.getGameProfile().getId());
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if(panel != null) {
            panel.handleMouseInput();
        }
        if(tabs != null) {
            int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            tabs.handleMouseInput(i, j);
        }
    }

}
