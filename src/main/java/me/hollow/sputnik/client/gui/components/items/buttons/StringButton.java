package me.hollow.sputnik.client.gui.components.items.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.ClientEvent;
import me.hollow.sputnik.client.gui.TrollGui;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;

public class StringButton extends Button {

    private final Setting setting;
    public boolean isListening;
    private CurrentString currentString = new CurrentString("");

    public StringButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        width = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7F, y + height - 0.5f, getColor(isHovering(mouseX, mouseY)));
        if(isListening) {
            Main.fontManager.drawString(currentString.getString() + "_", x + 2F, y - 1F - TrollGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        } else {
            Main.fontManager.drawString((setting.getName().equals("Buttons") ? "Buttons " : (setting.getName().equals("Prefix") ? "Prefix  " + ChatFormatting.GRAY : "")) + setting.getValue(), x + 2F, y - 1F - TrollGui.getClickGui().getTextOffset(), getState() ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    //TODO: CHINESE
    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if(isListening) {
            switch (keyCode) {
                case 1:
                    break;
                case 28:
                    enterString();
                    break;
                case 14:
                    setString(removeLastChar(currentString.getString()));
                    break;
                default:
                    if(ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        setString(currentString.getString() + typedChar);
                    }
            }
        }
    }

    @Override
    public void update() {
        this.setHidden(!setting.isVisible());
    }

    private void enterString() {
        if(currentString.getString().isEmpty()) {
            setting.setValue(setting.getDefaultValue());
        } else {
            setting.setValue(currentString.getString());
        }
        setString("");
        super.onMouseClick();
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void toggle() {
        isListening = !isListening;
    }

    public boolean getState() {
        return !isListening;
    }

    public void setString(String newString) {
        Main.INSTANCE.getBus().post(new ClientEvent(null));
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    //TODO: WTF IS THIS
    public static class CurrentString {
        private final String string;

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
