package me.hollow.sputnik.client.gui.components;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.interfaces.Minecraftable;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.gui.components.items.Item;
import me.hollow.sputnik.client.gui.components.items.buttons.Button;
import me.hollow.sputnik.client.modules.client.ClickGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Component implements Minecraftable {

    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    public boolean drag;
    private final List<Item> items = new ArrayList<>();
    private boolean hidden = false;
    private final String name;


    public Component(String name, int x, int y, boolean open) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 18;
        this.open = open;
        setupItems();
    }

    public final String getName() {
        return name;
    }

    public void setupItems() {}

    private void drag(int mouseX, int mouseY) {
        if (!drag) {
            return;
        }
        x = x2 + mouseX;
        y = y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drag(mouseX, mouseY);
        float totalItemHeight = open ? getTotalItemHeight() - 2F : 0F;
        RenderUtil.drawRect(x - 1, y - 2, x + width + 1, y + height - 6, new Color(ClickGui.getInstance().categoryRed.getValue(), ClickGui.getInstance().categoryGreen.getValue(), ClickGui.getInstance().categoryBlue.getValue(), ClickGui.getInstance().categoryAlpha.getValue()).getRGB());
        if (open) {
            RenderUtil.drawRect(x, y + 12F, x + width, y + height + totalItemHeight, new Color(0, 0, 0, ClickGui.getInstance().alpha.getValue()).getRGB());
        }
        Main.fontManager.drawString(getName(), x + (float)width / 2 - Main.fontManager.getStringWidth(getName()) / 2, y - 4F - TrollGui.getClickGui().getTextOffset(), 0xFFFFFF);
        if (open) {
            float y = getY() + getHeight() - 4F;
            for (int i = 0; i < getItems().size(); ++i) {
                final Item item = getItems().get(i);
                if (!item.isHidden()) {
                    item.setLocation(x + 2F, y);
                    item.setWidth(getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += item.getHeight() + 1F;
                }
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            x2 = x - mouseX;
            y2 = y - mouseY;
            for (Component component : TrollGui.getClickGui().getComponents()) {
                if (component.drag) {
                    component.drag = false;
                }
            }
            drag = true;
            return;
        }
        if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
            open = !open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_FALL, 10.0F));
            return;
        }
        if (!open) {
            return;
        }
        for (Item item : getItems()) {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(final int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            drag = false;
        if (!open) {
            return;
        }
        for (Item item : getItems()) {
            item.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!open) {
            return;
        }
        for (Item item : getItems()) {
            item.onKeyTyped(typedChar, keyCode);
        }
    }

    public void addButton(Button button) {
        items.add(button);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isOpen() {
        return open;
    }

    public final List<Item> getItems() {
        return items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0;
        for (Item item : getItems()) {
            height += item.getHeight() + 1.5F;
        }
        return height;
    }
}