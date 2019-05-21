package its_meow.claimitgui.client.gui.objects;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public abstract class GuiScrollPanel<T> extends GuiSlot {

    public ArrayList<T> contents = new ArrayList<T>();
    public int selectedIndex = -1;

    public GuiScrollPanel(int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        super(Minecraft.getMinecraft(), width, height, topIn, bottomIn, slotHeightIn);
    }
    
    @Override
    public int getListWidth() {
        return this.width - 15;
    }

    @Override
    protected int getScrollBarX() {
        return this.width - 6;
    }

    @Override
    protected int getSize() {
        return contents.size();
    }

    public void addItem(T item) {
        contents.add(item);
    }
    
    public void clearItems() {
        contents.clear();
    }
    
    public void removeItem(T item) {
        contents.remove(item);
    }
    
    @Nullable
    public T getSelectedItem() {
        return isSelectionValid() ? contents.get(selectedIndex) : null;
    }
    
    protected boolean isSelectionValid() {
        return selectedIndex >= 0 && selectedIndex < contents.size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        selectedIndex = slotIndex;
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return slotIndex == selectedIndex;
    }

    @Override
    protected void drawBackground() {}
    
    @Override
    protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
        if(slotIndex >= 0 && slotIndex < contents.size()) {
            this.drawSlot(slotIndex, xPos, yPos, heightIn, mouseXIn, mouseYIn, partialTicks, contents.get(slotIndex));
        }
    }
    
    protected abstract void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks, T item);

}