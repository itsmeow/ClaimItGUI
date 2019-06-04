package its_meow.claimitgui.client.gui;

import java.io.IOException;
import java.util.UUID;

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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetworkPlayerInfo;
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
    public static final int PERMISSION_TAB_BUTTON_ID = 1;
    public static final int GROUP_TAB_BUTTON_ID = 2;

    // General
    public ClaimArea lastClaim = null;
    public GuiButton claimDeleteButton;

    // Popup prompt
    public GuiButton okPromptButton;
    public boolean hasResult = false;
    public boolean lastHasResult = false;
    public DeletionResult result;

    // Scrol list
    public int permScrollIndex = 0;
    public int permScrollMax = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw background and panel
        this.drawDefaultBackground();
        this.panel.drawScreen(mouseX, mouseY, partialTicks);
        // Draw top layer of selection panel
        this.drawString(mc.fontRenderer, "Claims", 10, 10, 0xeeee00);
        refreshButton.drawButton(mc, mouseX, mouseY, partialTicks);
        // Get claim and start claim-based renders
        ClaimArea claim = panel.getSelectedItem();
        if(claim != null) {

            // Draw backgrounds

            // Initialize positioning
            int xOff = 5;
            int yOff = 3;
            int mainPanelRight = (int) ((width * 0.66666F) - xOff);
            String title = "Claim: " + claim.getDisplayedViewName();
            int titleWidth = (int) (mc.fontRenderer.getStringWidth(title) * 1.5F);
            int topBarBottom = (int) (yOff + 8F + (mc.fontRenderer.FONT_HEIGHT * 1.5F));
            //int titleBarX = (int) (panelWidth + xOff + 8F + titleWidth);

            // Draw location / left panel
            drawRect(panelWidth + xOff, yOff, mainPanelRight, height - yOff, TITLE_BOX_COLOR);
            drawRect(panelWidth + xOff, yOff, mainPanelRight, 2 * topBarBottom, MAIN_BOX_COLOR);
            // Outline
            drawHorizontalLine(panelWidth + xOff, mainPanelRight, yOff, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, mainPanelRight, 2 * topBarBottom, 0xff141414);
            drawHorizontalLine(panelWidth + xOff, mainPanelRight, height - yOff, 0xff141414);
            drawVerticalLine(panelWidth + xOff, yOff, height - yOff, 0xff141414);
            drawVerticalLine(mainPanelRight, yOff, height - yOff, 0xff141414);

            // Draw right panel
            drawRect(mainPanelRight + xOff, yOff, width - xOff, height - yOff, TITLE_BOX_COLOR);
            drawRect(mainPanelRight + xOff, yOff, width - xOff, topBarBottom + 1, MAIN_BOX_COLOR);
            // Outline
            drawHorizontalLine(mainPanelRight + xOff, width - xOff, yOff, 0xff141414);
            drawHorizontalLine(mainPanelRight + xOff, width - xOff, topBarBottom, 0xff141414);
            drawHorizontalLine(mainPanelRight + xOff, width - xOff, height - yOff, 0xff141414);
            drawVerticalLine(mainPanelRight + xOff, yOff, height - yOff, 0xff141414);
            drawVerticalLine(width - xOff, yOff, height - yOff, 0xff141414);


            // Draw behind claim title
            drawRect(panelWidth + xOff + 1, yOff + 1, mainPanelRight, topBarBottom, 0xff626262);


            // Start draw foregrounds

            // Draw claim title
            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                this.mc.fontRenderer.drawStringWithShadow(title, (panelWidth + xOff + 3) / 1.5F, yOff + 3, 0xffffff);
            }
            GlStateManager.popMatrix();

            // Draw line below title
            drawHorizontalLine(panelWidth + xOff + 1, mainPanelRight, topBarBottom - 1, 0xff141414);

            // Add tabs for right panel
            if(tabs == null) {
                tabs = new GuiTabSelection(mc, mainPanelRight + xOff + 1, width - xOff, yOff + 1);
                tabs.addButton(PERMISSION_TAB_BUTTON_ID, "Permissions");
                tabs.addButton(GROUP_TAB_BUTTON_ID, "Groups");
            }

            // Update if claim changes due to title sizing
            if(claim != lastClaim) {
                tabs.setDimensions(mainPanelRight + xOff + 1, width - xOff, yOff + 1);
            }
            // Draw tabs
            tabs.drawScreen(mouseX, mouseY, partialTicks);

            // Set up for drawing player name
            String name = claim.getOwner().toString();
            String oName = name;
            float scaleO = 0.8F;
            int nameWidth = 0;

            // Draw player name and fill nameWidth with the width in pixels
            GlStateManager.pushMatrix();
            {
                GlStateManager.scale(scaleO, scaleO, scaleO);
                name = this.getPlayerName(claim.getOwner());
                oName = name;
                while((mc.fontRenderer.getStringWidth("Owner: " + name)) * scaleO >= titleWidth) {
                    name = name.substring(0, name.length() - 1);
                }
                if(name.length() != oName.length()) {
                    name += "...";
                }
                nameWidth = (int) (mc.fontRenderer.getStringWidth("Owner: " + name) * scaleO);
                this.mc.fontRenderer.drawStringWithShadow("Owner: " + name, (panelWidth + xOff + 3) / scaleO, (topBarBottom + 3) / scaleO, 0xffffff);
            }
            GlStateManager.popMatrix();

            // Set up and draw delete button
            if(claimDeleteButton == null) {
                claimDeleteButton = new GuiButton(2, panelWidth + xOff + nameWidth + 10, topBarBottom + 2, mainPanelRight - (panelWidth + xOff + nameWidth + 10), 20, "Delete");
                this.addButton(claimDeleteButton);
            } else {
                claimDeleteButton.setWidth(mainPanelRight - (panelWidth + xOff + nameWidth + 10));
                claimDeleteButton.x = panelWidth + xOff + nameWidth + 10;
                claimDeleteButton.drawButton(mc, mouseX, mouseY, partialTicks);
            }

            // Draw location information in left pane
            this.drawString(mc.fontRenderer, "Corners", panelWidth + xOff + 2, 4 + (2 * topBarBottom), 0xffffff);
            BlockPos[] corners = claim.getFourCorners();
            for(int i = 0; i < corners.length; i++) {
                this.drawString(mc.fontRenderer, corners[i].getX() + ", " + corners[i].getZ(), panelWidth + xOff + 2, (2 * topBarBottom) + 15 + (10 * i), 0xffffff);
            }
            this.drawString(mc.fontRenderer, "Dimension: " + claim.getDimensionID(), mainPanelRight - 2 - mc.fontRenderer.getStringWidth("Dimension:  " + claim.getDimensionID()), (2 * topBarBottom) + 4, 0xffffff);

            // Draw permissions if selected
            if(tabs.getSelectedID() == PERMISSION_TAB_BUTTON_ID) {
                permScrollMax = claim.getMembers().keySet().size();
                int i = 0;
                int k = 0;
                for(UUID uuid : claim.getMembers().keySet()) {
                    if(i >= permScrollIndex && i <= permScrollMax) {
                        this.drawString(mc.fontRenderer, uuid.toString(), mainPanelRight + xOff + 2, topBarBottom + 15 + (10 * k), 0xffffff);
                        k++;
                    }
                    i++;
                }
                if(i > 0) {
                    this.drawString(mc.fontRenderer, "Permissions:", mainPanelRight + xOff + 2, 4 + topBarBottom, 0xffffff);
                } else {
                    this.drawString(mc.fontRenderer, "No Permissions Added.", mainPanelRight + xOff + 2, 4 + topBarBottom, 0xffffff);
                }
            }

            // Draw hover dialog for large player names
            if(name.length() != oName.length() && mouseY >= (topBarBottom + 3) * scaleO && mouseY <= (topBarBottom + 3 + 20) * scaleO && mouseX >= (panelWidth + xOff + 3) && mouseX <= (panelWidth + xOff + 3 + titleWidth)) {
                int width = mc.fontRenderer.getStringWidth("Owner: " + oName + "  ");
                int left = mouseX - width;
                int right = mouseX;
                // Reverse the dialog if it doesn't fit on-screen
                if(mouseX - width < 0) {
                    left = mouseX;
                    right = mouseX + width;
                }
                int height = 15;
                drawRect(left, mouseY, right, mouseY + height, MAIN_BACKGROUND_COLOR_2);
                drawVerticalLine(left, mouseY, mouseY + height, MAIN_BACKGROUND_COLOR);
                drawVerticalLine(right, mouseY, mouseY + height, MAIN_BACKGROUND_COLOR);
                drawHorizontalLine(left, right, mouseY, MAIN_BACKGROUND_COLOR);
                drawHorizontalLine(left, right, mouseY + height, MAIN_BACKGROUND_COLOR);
                this.drawString(mc.fontRenderer, "Owner: " + oName, left + 5, mouseY + 5, 0xffffff);
            }
        } else {
            this.drawCenteredString(mc.fontRenderer, "No claim selected.", (width - (width / 3)), 15, 0xe3e3e3);
        }

        // Draw result panel
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

        // Refresh after result pane is closed or opened
        if(lastHasResult ^ hasResult) {
            ClaimItGUI.NET.sendToServer(new CRefreshListPacket());
            this.resetClaimList();
        }

        // Initialize next render
        lastHasResult = hasResult;
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
        resetClaimList();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClaimAdded(ClientClaimAddedEvent event) {
        panel.addItem(event.getClaim());
    }

    @SubscribeEvent
    public void onClaimRemoved(ClientClaimRemovedEvent event) {
        resetClaimList();
    }

    public void resetClaimList() {
        panel.clearItems();
        for(ClaimArea claim : ClientClaimManager.getClaimsList()) {
            panel.addItem(claim);
        }
    }

    @SubscribeEvent
    public void onDeletionResult(ClaimDeletionResultEvent event) {
        this.result = event.result;
        this.hasResult = true;
        if(event.claim != null && (event.result == DeletionResult.DELETED || event.result == DeletionResult.NO_EXIST)) {
            MinecraftForge.EVENT_BUS.post(new ClientClaimRemovedEvent(event.claim));
        }
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
            if(panel != null && panel.getSelectedItem() != null && (button == claimDeleteButton || button.id == 2)) {
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
        if(!hasResult && tabs != null && tabs.getSelectedID() == PERMISSION_TAB_BUTTON_ID) {
            int scrollDirection = Mouse.getEventDWheel();

            if (scrollDirection != 0)
            {
                if (scrollDirection > 0)
                {
                    scrollDirection = -1;
                }
                else if (scrollDirection < 0)
                {
                    scrollDirection = 1;
                }

                this.permScrollIndex += scrollDirection;
                if(permScrollIndex < 0) {
                    permScrollIndex = 0;
                }
                if(permScrollIndex > permScrollMax) {
                    permScrollIndex = permScrollMax;
                }
            }
        }
    }

    public String getPlayerName(UUID uuid) {
        NetworkPlayerInfo info = this.mc.player.connection.getPlayerInfo(uuid);
        if(info == null) {
            return uuid.toString();
        }
        return info.getDisplayName() != null ? info.getDisplayName().getFormattedText() : info.getGameProfile().getName();
    }

}
