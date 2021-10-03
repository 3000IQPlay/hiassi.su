package me.hollow.sputnik.client.gui;


import me.hollow.sputnik.Main;
import me.hollow.sputnik.client.gui.components.Component;
import me.hollow.sputnik.client.gui.components.items.Item;
import me.hollow.sputnik.client.gui.components.items.buttons.ModuleButton;
import me.hollow.sputnik.client.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class TrollGui extends GuiScreen {

    private final ArrayList<Component> components = new ArrayList<>();
    private static TrollGui INSTANCE = new TrollGui();

    public TrollGui() {
        INSTANCE = this;
        load();
    }

    public static TrollGui getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TrollGui();
        }
        return INSTANCE;
    }

    public static TrollGui getClickGui() {
        return getInstance();
    }

    private void load() {
        int x = -95;
        for (final Module.Category category : Module.Category.values()) {
            components.add(new Component(category.name(), x += 105  , 4, true) {
                @Override
                public void setupItems() {
                    for (Module module : Main.INSTANCE.getModuleManager().getModulesByCategory(category)) {
                        addButton(new ModuleButton(module));
                    }
                }
            });
        }
        for (Component component : components) {
            component.getItems().sort(Comparator.comparing(Item::getName));
        }
    }

    public void updateModule(Module module) {
        for (Component component : components) {
            for (final Item item : component.getItems()) {
                if (item instanceof ModuleButton) {
                    final ModuleButton button = (ModuleButton) item;
                    final Module mod = button.getModule();
                    if (module != null && module.equals(mod)) {
                        button.initSettings();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        checkMouseWheel();
        for (Component component : components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        for (Component component : components) {
            component.mouseClicked(mouseX, mouseY, clickedButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        for (Component component : components) {
            component.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            for (Component component : components) {
                component.setY(component.getY() - 10);
            }
        } else if (dWheel > 0) {
            for (Component component : components) {
                component.setY(component.getY() + 10);
            }
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for(Component component : this.components) {
            if(component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Component component : components) {
            component.onKeyTyped(typedChar, keyCode);
        }
    }
}