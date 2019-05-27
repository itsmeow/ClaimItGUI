package its_meow.claimitgui.client.gui;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimitgui.ClaimItGUI;
import its_meow.claimitgui.client.ClientClaimManager;
import its_meow.claimitgui.client.event.ClaimDeletionResultEvent;
import its_meow.claimitgui.client.event.ClientClaimAddedEvent;
import its_meow.claimitgui.client.event.ClientClaimRemovedEvent;
import its_meow.claimitgui.client.gui.objects.GuiScrollClaimPanel;
import its_meow.claimitgui.client.gui.objects.GuiTabSelection;
import its_meow.claimitgui.network.CDeleteClaimPacket;
import its_meow.claimitgui.network.CRefreshListPacket;
import its_meow.claimitgui.network.SClaimDeletionResultPacket.DeletionResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    public GuiButton refreshButton;
    public static final int LOCATION_TAB_BUTTON_ID = 0;
    public static final int PERMISSION_TAB_BUTTON_ID = 1;
    public static final int GROUP_TAB_BUTTON_ID = 2;

    // General
    public ClaimArea lastClaim = null;
    public GuiButton claimDeleteButton;

    // Popup prompt
    public GuiButton okPromptButton;
    public boolean hasResult = false;
    public DeletionResult result;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.drawBackground(0);
        this.drawDefaultBackground();
        //drawGradientRect(0, 0, width, height, MAIN_BACKGROUND_COLOR, MAIN_BACKGROUND_COLOR_2);
        this.panel.drawScreen(mouseX, mouseY, partialTicks);
        //drawVerticalLine(panelWidth, -1, height, 0xff2d2d2d);
        //drawVerticalLine(panelWidth + 1, -1, height, 0xff2d2d2d);
        this.drawString(mc.fontRenderer, "Claims", 10, 10, 0xeeee00);
        refreshButton.drawButton(mc, mouseX, mouseY, partialTicks);
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
                tabs.addButton(GROUP_TAB_BUTTON_ID, "Groups");
            }
            if(claimDeleteButton == null) {
                claimDeleteButton = new GuiButton(2, titleBarX + 2, topBarBottom + 2, (width - xOff - titleBarX) / 3, 20, "Delete");
                this.addButton(claimDeleteButton);
            } else {
                claimDeleteButton.setWidth((width - xOff - titleBarX) / 3);
                claimDeleteButton.x = titleBarX + 2;
                claimDeleteButton.drawButton(mc, mouseX, mouseY, partialTicks);
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
                int maxL = 15;
                if(name.length() > maxL) {
                    name = name.substring(0, maxL);
                    name += "...";
                }
                this.mc.fontRenderer.drawStringWithShadow("Owner: " + name, (panelWidth + xOff + 3) / scaleO, (topBarBottom + 3) / scaleO, 0xffffff);
            }
            GlStateManager.popMatrix();
            if(tabs.getSelectedID() == LOCATION_TAB_BUTTON_ID) {
                this.drawString(mc.fontRenderer, "Corners", panelWidth + xOff + 2, 4 + (2 * topBarBottom), 0xffffff);
                BlockPos[] corners = claim.getFourCorners();
                for(int i = 0; i < corners.length; i++) {
                    this.drawString(mc.fontRenderer, corners[i].getX() + ", " + corners[i].getZ(), panelWidth + xOff + 2, (2 * topBarBottom) + 15 + (10 * i), 0xffffff);
                }
                this.drawString(mc.fontRenderer, "Dimension: " + claim.getDimensionID(), width - xOff - 2 - mc.fontRenderer.getStringWidth("Dimension:  " + claim.getDimensionID()), (2 * topBarBottom) + 4, 0xffffff);
            }
        } else {
            this.drawCenteredString(mc.fontRenderer, "No claim selected.", (width - (width / 3)), 15, 0xe3e3e3);
        }
        if(hasResult) {
            this.drawDefaultBackground();
            int boxWidth = 200;
            int boxHeight = 50;
            int x = (this.width / 2) - (boxWidth / 2);
            int y = (this.height / 2) - (boxHeight / 2);
            drawRect(x, y, x + boxWidth, y + boxHeight, MAIN_BACKGROUND_COLOR_2);
            drawRect(x + 1, y + 1, x + boxWidth - 1, y + boxHeight - 1, MAIN_BACKGROUND_COLOR);
            this.drawCenteredString(mc.fontRenderer, this.result.toString(), this.width / 2, (this.height / 2) - (boxHeight / 3), 0xffffff);
            this.okPromptButton.x = (this.width / 2) - (this.okPromptButton.width / 2);
            this.okPromptButton.y = (this.height / 2);
            this.okPromptButton.drawButton(mc, mouseX, mouseY, partialTicks);
        }

        lastClaim = claim;
    }

    @Override
    public void initGui() {
        panelWidth = (int) (this.width / 3F);
        panel = new GuiScrollClaimPanel(panelWidth, this.height, 25, this.height, BACKGROUND_COLOR, BACKGROUND_COLOR_2);
        refreshButton = new GuiButton(0, 40, 3, 80, 20,"Refresh");
        this.addButton(refreshButton);
        okPromptButton = new GuiButton(1, this.width / 2 - 100, (this.height / 2) + 20, 100, 20, "OK");
        this.addButton(okPromptButton);
        /*for(int i = 0; i < 50; i++) {
            panel.addItem(randClaim());
        }*/
        ClaimItGUI.NET.sendToServer(new CRefreshListPacket());
        panel.clearItems();
        for(ClaimArea claim : ClientClaimManager.getClaimsList()) {
            panel.addItem(claim);
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClaimAdded(ClientClaimAddedEvent event) {
        panel.addItem(event.getClaim());
    }

    @SubscribeEvent
    public void onClaimRemoved(ClientClaimRemovedEvent event) {
        panel.clearItems();
        for(ClaimArea claim : ClientClaimManager.getClaimsList()) {
            panel.addItem(claim);
        }
    }

    @SubscribeEvent
    public void onDeletionResult(ClaimDeletionResultEvent event) {
        this.result = event.result;
        this.hasResult = true;
        this.onClaimRemoved(new ClientClaimRemovedEvent(null));
    }

    /*private static ClaimArea randClaim() {
        return new ClaimArea((int) (Math.random() * 10), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), (int) (Math.random() * 100), Minecraft.getMinecraft().player.getGameProfile().getId());
    }*/

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(!hasResult) {
            if(button == refreshButton) {
                ClaimItGUI.NET.sendToServer(new CRefreshListPacket());
                panel.clearItems();
                for(ClaimArea claim : ClientClaimManager.getClaimsList()) {
                    panel.addItem(claim);
                }
            }
            if(panel != null && panel.getSelectedItem() != null && button == claimDeleteButton) {
                ClaimItGUI.NET.sendToServer(new CDeleteClaimPacket(panel.getSelectedItem()));
            }
        } else {
            if(button == okPromptButton) {
                this.hasResult = false;
            }
        }
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
