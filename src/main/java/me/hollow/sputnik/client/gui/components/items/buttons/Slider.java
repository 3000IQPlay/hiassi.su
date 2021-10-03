package me.hollow.sputnik.client.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.gui.components.Component;
import org.lwjgl.input.Mouse;

public class Slider extends Button {

    public final Setting setting;
    private final Number min;
    private final Number max;
    private final int difference;

    public Slider(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = (Number)setting.getMin();
        this.max = (Number)setting.getMax();
        this.difference = max.intValue() - min.intValue();
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width + 7F, y + height - 0.5f, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        RenderUtil.drawRect(x, y, ((Number)setting.getValue()).floatValue() <= min.floatValue() ? x : x + (width + 7F) * partialMultiplier(), y + height - 0.5f, 0x70555555);
        Main.fontManager.drawString(getName() + " " + ChatFormatting.GRAY + (setting.getValue() instanceof Float ? ((Number)setting.getValue()) : ((Number)setting.getValue()).doubleValue()), x + 2F, y - 1F - TrollGui.getClickGui().getTextOffset(), 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : TrollGui.getClickGui().getComponents()) {
            if (component.drag) {
                return false;
            }
        }
        return mouseX >= getX() && mouseX <= getX() + getWidth() + 8 && mouseY >= getY() && mouseY <= getY() + height;
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    private void dragSetting(int mouseX, int mouseY) {
        if(isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSettingFromX(mouseX);
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    private void setSettingFromX(int mouseX) {
        float percent = (mouseX - x) / (width + 7.4F);
        if(setting.getValue() instanceof Double) {
            double result = (Double)setting.getMin() + (difference * percent);
            setting.setValue(Math.round(10.0 * result) / 10.0);
        } else if (setting.getValue() instanceof Float) {
            float result = (Float)setting.getMin() + (difference * percent);
            setting.setValue(Math.round(10.0f * result) / 10.0f);
        } else if (setting.getValue() instanceof Integer) {
            setting.setValue(((Integer)setting.getMin() + (int)(difference * percent)));
        }
    }

    private float middle() {
        return max.floatValue() - min.floatValue();
    }

    private float part() {
        return ((Number)setting.getValue()).floatValue() - min.floatValue();
    }

    private float partialMultiplier() {
        return part() / middle();
    }
}
