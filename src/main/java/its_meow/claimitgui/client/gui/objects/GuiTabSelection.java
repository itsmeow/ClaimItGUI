package its_meow.claimitgui.client.gui.objects;

import java.util.List;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiTabSelection {
    protected final Minecraft mc;
    public int top;
    public int right;
    public int left;

    protected boolean visible = true;

    private boolean enabled = true;

    protected List<GuiButton> buttonList = Lists.<GuiButton>newArrayList();

    protected GuiButton selection = null;

    public GuiTabSelection(Minecraft mcIn, int left, int right, int top) {
        this.mc = mcIn;
        this.top = top;
        this.left = left;
        this.right = right;
    }

    public void setDimensions(int left, int right, int top) {
        this.top = top;
        this.left = left;
        this.right = right;
        int i = 0;
        int tabWidth = (right - left) / this.buttonList.size();
        for(GuiButton button : this.buttonList) {
            button.x = left + (tabWidth * i);
            button.y = top;
            button.width = tabWidth;
            button.height = 20;
            i++;
        }
        if(selection != null) {
            this.actionPerformed(selection);
        }
    }

    public void addButton(int id, String name) {
        GuiButton button = new GuiButton(id, 0, 0, 0, 0, name);
        if(buttonList.size() == 0) {
            this.selection = button;
        }
        this.buttonList.add(button);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0) {
            for(GuiButton button : this.buttonList) {
                if(button.mousePressed(this.mc, mouseX, mouseY)) {
                    button.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(button);
                }
            }
        }
    }

    protected void actionPerformed(GuiButton button) {
        buttonList.forEach(b -> b.enabled = true);
        button.enabled = false;
        this.selection = button;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            for(GuiButton button : this.buttonList) {
                button.drawButton(mc, mouseX, mouseY, partialTicks);
            }
        }
    }

    public int getSelectedID() {
        if(selection == null) {
            return -1;
        } else {
            return selection.id;
        }
    }

    public void setEnabled(boolean enabledIn) {
        this.enabled = enabledIn;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void handleMouseInput(int mouseX, int mouseY) {
        if(Mouse.getEventButtonState()) {
            this.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
        }
    }

}