package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;
import tcb.bces.listener.Subscribe;

import java.awt.*;

@ModuleManifest(label = "ClickGui", category = Module.Category.CLIENT, key = Keyboard.KEY_RCONTROL)
public class ClickGui extends Module {

    public final Setting<String> prefix = register(new Setting<>("Prefix", "."));

    public final Setting<Boolean> customFov = register(new Setting<>("Custom Fov", false));
    public final Setting<Float> fov = register(new Setting<>("Fov", 150.0f, -180.0f, 180.0f));

    public final Setting<Integer> red = register(new Setting<>("Red", 255, 0, 255));
    public final Setting<Integer> green = register(new Setting<>("Green", 120, 0, 255));
    public final Setting<Integer> blue = register(new Setting<>("Blue", 255, 0, 255));

    public final Setting<Integer> hoverAlpha = register(new Setting<>("Hover Alpha", 60, 0, 255));
    public final Setting<Integer> enabledAlpha = register(new Setting<>( "Enabled Alpha", 255, 0, 255));

    public final Setting<Integer> categoryRed = register(new Setting<>("Category Red", 255, 0, 255));
    public final Setting<Integer> categoryGreen = register(new Setting<>("Category Green", 50, 0, 255));
    public final Setting<Integer> categoryBlue = register(new Setting<>("Category Blue", 255, 0, 255));
    public final Setting<Integer> categoryAlpha = register(new Setting<>("Category Alpha", 210, 0, 255));

    public final Setting<Integer> alpha = register(new Setting<>("Alpha", 120, 0, 255));

    private static ClickGui INSTANCE;

    public ClickGui() {
        INSTANCE = this;
    }

    public static ClickGui getInstance() {
        return INSTANCE;
    }

    public final int getColor(boolean hover) {
        return new Color(red.getValue(), green.getValue(), blue.getValue(), hover ? hoverAlpha.getValue() : enabledAlpha.getValue()).getRGB();
    }

    @Subscribe
    public void onTick(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (!(mc.currentScreen instanceof TrollGui)) {
            setEnabled(false);
        }

        if (customFov.getValue()) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());
        }
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.displayGuiScreen(new TrollGui());
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof TrollGui) {
            mc.displayGuiScreen(null);
        }
    }

}
