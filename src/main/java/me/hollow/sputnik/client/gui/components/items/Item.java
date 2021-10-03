package me.hollow.sputnik.client.gui.components.items;


import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.client.modules.client.ClickGui;

import java.awt.*;

public class Item implements Minecraftable {

    protected float x, y;
    protected int width, height;
    private boolean hidden;

    final String name;

    public Item(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public int getColor(boolean hovered) {
        return !hovered ? 0x11555555 : 0x88555555;
    }

    public int getColor() {
        return new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue()).getRGB();
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks){}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton){}

    public void mouseReleased(int mouseX, int mouseY, int releaseButton){}

    public void update(){}

    public void onKeyTyped(char typedChar, int keyCode) {}

    public final float getX() {
        return x;
    }

    public final float getY() {
        return y;
    }

    public final int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
