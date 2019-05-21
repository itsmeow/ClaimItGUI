package its_meow.claimitgui.client.gui.objects;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiScrollClaimPanel extends GuiScrollPanel<ClaimArea> {

    protected final int backgroundColor;
    protected final int backgroundColor2;

    public GuiScrollClaimPanel(int width, int height, int topIn, int bottomIn, int backgroundColor, int backgroundColor2) {
        super(width, height, topIn, bottomIn, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 2);
        this.backgroundColor = backgroundColor;
        this.backgroundColor2 = backgroundColor2;
    }

    @Override
    protected void drawBackground() {
        drawGradientRect(this.left, this.top, this.right, this.bottom, this.backgroundColor, this.backgroundColor2);
    }
    
    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks, ClaimArea claim) {
        mc.fontRenderer.drawString(String.valueOf(slotIndex  + 1), 10, yPos, 0x6b6b6b);
        mc.fontRenderer.drawString(claim.getDisplayedViewName(), 35, yPos, 0xe4e4e4);
    }

}
