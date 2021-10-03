package me.hollow.sputnik.client.gui.components.items.buttons;


import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.gui.components.items.Item;
import me.hollow.sputnik.client.modules.Module;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {

    private final Module module;
    private List<Item> items = new ArrayList<>();
    private boolean subOpen;

    public ModuleButton(Module module) {
        super(module.getLabel());
        this.module = module;
        initSettings();
    }

    public void initSettings() {
        List<Item> newItems = new ArrayList<>();
        if (!this.module.getSettings().isEmpty()) {
            for (Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }

                if(setting.getValue() instanceof Bind) {
                    newItems.add(new BindButton(setting));
                }

                if(setting.getValue() instanceof String || setting.getValue() instanceof Character) {
                    newItems.add(new StringButton(setting));
                }

                if(setting.isNumberSetting()) {
                    if(setting.hasRestriction()) {
                        newItems.add(new Slider(setting));
                        continue;
                    }
                    newItems.add(new UnlimitedSlider(setting));
                }

                if (setting.isEnumSetting()) {
                    newItems.add(new EnumButton(setting));
                }
            }
        }
        this.items = newItems;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height - 0.5f, getColor(isHovering(mouseX, mouseY)));
        Main.fontManager.drawString(getName(), x + 2F, y - 2F - TrollGui.getClickGui().getTextOffset(), module.isEnabled() ? getColor() : 0xFFFFFFFF);
        if (!this.items.isEmpty()) {
            //JordoHack.fontManager.drawString(subOpen ? ClickGui.getInstance().buttonClose.getValueAsString() : ClickGui.getInstance().buttonOpen.getValue(), x - 1.5F + width - 7.4F, y - 2F - TrollGui.getClickGui().getTextOffset(), 0xFFFFFFFF);
            if (subOpen) {
                float height = 1;
                for (final Item item : items) {
                    if (!item.isHidden()) {
                        height += 15F;
                        item.setLocation(x + 1, y + height);
                        item.setHeight(15);
                        item.setWidth(width - 9);
                        item.drawScreen(mouseX, mouseY, partialTicks);
                    }
                    item.update();
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
                subOpen = !subOpen;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_FALL, 10.0F));
            }

            if (subOpen) {
                for (Item item : this.items) {
                    if(!item.isHidden()) {
                        item.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && subOpen) {
            for (Item item : this.items) {
                if(!item.isHidden()) {
                    item.onKeyTyped(typedChar, keyCode);
                }
            }
        }
    }

    @Override
    public int getHeight() {
        if (subOpen) {
            int height = 14;
            for (Item item : this.items) {
                if(!item.isHidden()) {
                    height += item.getHeight() + 1;
                }
            }
            return height + 2;
        } else {
            return 14;
        }
    }

    public Module getModule() {
        return this.module;
    }

    public void toggle() {
        module.toggle();
    }

    public boolean getState() {
        return module.isEnabled();
    }
}

