package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import me.hollow.sputnik.api.util.Wrapper;


@ModuleManifest(label = "GuiMove", category = Module.Category.MISC, color = 0x00FF00)
public class GuiMove extends Module {

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiChat || mc.currentScreen == null) {
            return;
        }
        final int[] keys = new int[]{mc.gameSettings.keyBindForward.getKeyCode(), mc.gameSettings.keyBindLeft.getKeyCode(), mc.gameSettings.keyBindRight.getKeyCode(), mc.gameSettings.keyBindBack.getKeyCode(), mc.gameSettings.keyBindJump.getKeyCode()};

        for (int keyCode : keys) {
            if (Keyboard.isKeyDown(keyCode)) {
                KeyBinding.setKeyBindState(keyCode, true);
            } else {
                KeyBinding.setKeyBindState(keyCode, false);
            }
        }

        if (mc.getMinecraft().currentScreen instanceof GuiContainer)
        {
            if (Keyboard.isKeyDown(Integer.valueOf(200).intValue())) {
                mc.getMinecraft().player.rotationPitch -= 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(208).intValue())) {
                mc.getMinecraft().player.rotationPitch += 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(205).intValue())) {
                mc.getMinecraft().player.rotationYaw += 7.0F;
            }
            if (Keyboard.isKeyDown(Integer.valueOf(203).intValue())) {
                mc.getMinecraft().player.rotationYaw -= 7.0F;
            }
            if(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindSprint.getKeyCode())) {
                mc.getMinecraft().player.setSprinting(true);
            }
        }
    }
}

