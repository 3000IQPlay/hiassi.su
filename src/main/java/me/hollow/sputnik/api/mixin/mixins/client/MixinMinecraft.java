package me.hollow.sputnik.api.mixin.mixins.client;

import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.accessors.IMinecraft;
import me.hollow.sputnik.client.events.KeyEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.client.Manage;
import me.hollow.sputnik.client.modules.client.MiddleClick;
import me.hollow.sputnik.client.modules.misc.MultiTask;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = 1001)
public abstract class MixinMinecraft implements IMinecraft {

    @Override @Accessor
    public abstract void setRightClickDelayTimer(int delay);

    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void onKeyboard(CallbackInfo callbackInfo) {
        if (Keyboard.getEventKeyState()) {
            for (final Module m : Main.INSTANCE.getModuleManager().getModules()) {
                if (m.getKey() == Keyboard.getEventKey()) {
                    m.toggle();
                }
            }
            Main.INSTANCE.getBus().post(new KeyEvent(Keyboard.getEventKey()));
        }
    }

    @Inject(method = "runTickMouse", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void mouseClick(CallbackInfo ci) {
        if (Mouse.getEventButtonState()) {
            MiddleClick.getInstance().run(Mouse.getEventButton());
            MultiTask.getInstance().onMouse(Mouse.getEventButton());
        }
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void limitFps(CallbackInfoReturnable<Integer> cir) {
        if (Manage.INSTANCE.unfocusedLimit.getValue() && !Display.isActive()) {
            cir.setReturnValue(Manage.INSTANCE.unfocusedFPS.getValue());
        }
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        Main.INSTANCE.init();
    }

}
