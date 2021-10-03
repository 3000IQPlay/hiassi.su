package me.hollow.sputnik.client.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class EnumButton extends Button {

    public final Setting setting;

    public EnumButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7F, y + height - 0.5f, getColor(isHovering(mouseX, mouseY)));
        Main.fontManager.drawString(setting.getName() + " " + ChatFormatting.GRAY + setting.currentEnumName(), x + 2.3F, y - 1.7F - TrollGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void toggle() {
        setting.increaseEnum();
    }

    public boolean getState() {
        return true;
    }

}
