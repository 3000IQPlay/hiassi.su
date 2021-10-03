package me.hollow.sputnik.client.gui.components.items.buttons;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.gui.components.Component;
import me.hollow.sputnik.client.gui.components.items.Item;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;

public class Button extends Item {

    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height - 0.5f, getColor(isHovering(mouseX, mouseY)));
        Main.fontManager.drawString(getName(), x + 2F, y - 2F - TrollGui.getClickGui().getTextOffset(),  getState() ? getColor() : 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            onMouseClick();
        }
    }

    public void onMouseClick() {
        state = !state;
        toggle();
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_METAL_PLACE, 10.0F));
    }

    public void toggle() {}

    public boolean getState() {
        return state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        final ArrayList<Component> components = TrollGui.getClickGui().getComponents();
        for (int i = 0; i < components.size(); ++i) {
            if (components.get(i).drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + height;
    }
}
