package me.hollow.sputnik.client.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.property.Bind;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.gui.TrollGui;
import me.hollow.sputnik.client.modules.client.ChatNotifies;
import net.minecraft.client.Minecraft;
import tcb.bces.listener.IListener;

import java.util.ArrayList;
import java.util.List;

public class Module implements IListener {

    protected final Minecraft mc = Minecraft.getMinecraft();

    private final List<Setting> settings = new ArrayList<>();

    public final Setting<Boolean> drawn = register(new Setting<>("Drawn", true));
    public final Setting<Bind> bind = register(new Setting<>("Bind", new Bind(-10000)));
    public final Setting<Boolean> enabled = register(new Setting<>("Enabled", false));

    private String label, suffix;
    private Category category;
    private boolean persistent;
    private boolean listenable;
    private int color;

    public Module() {
        suffix = "";
        if (getClass().isAnnotationPresent(ModuleManifest.class)) {
            ModuleManifest moduleManifest = getClass().getAnnotation(ModuleManifest.class);
            label = moduleManifest.label();
            category = moduleManifest.category();
            bind.setValue(new Bind(moduleManifest.key()));
            drawn.setValue(moduleManifest.drawn());
            persistent = moduleManifest.persistent();
            listenable = moduleManifest.listen();
            color = moduleManifest.color();
            if (listenable)
                Main.INSTANCE.getBus().register(this);
        }
    }

    public final Setting register(Setting setting) {
        this.settings.add(setting);
        if (mc.currentScreen instanceof TrollGui) {
            TrollGui.getInstance().updateModule(this);
        }
        return setting;
    }

    public final List<Setting> getSettings() {
        return this.settings;
    }

    public final void setEnabled(boolean enabled) {
        if (persistent) {
            this.enabled.setValue(true);
            return;
        }

        this.enabled.setValue(enabled);
        onToggle();
        if (enabled) {
            onEnable();
            MessageUtil.sendClientMessage(ChatFormatting.WHITE + this.getLabel() + " \u00A7fwas \u00a72enabled", -44444);
        } else {
            onDisable();
            MessageUtil.sendClientMessage(ChatFormatting.WHITE + this.getLabel() + " \u00A7fwas \u00a74disabled", -44444);
        }
    }

    public void setDrawn(boolean drawn) {
        this.drawn.setValue(drawn);
    }

    public void toggle() {
        setEnabled(!enabled.getValue());
    }

    public void disable() {
        setEnabled(false);
    }

    public void onRender3D(){}
    public void onUpdate(){}
    public void onToggle(){}
    public void onEnable(){}
    public void onDisable(){}
    public void onLoad(){}
    public void onDisconnect(){}


    public final boolean isNull() {
        return mc.player == null || mc.world == null;
    }

    public final int getKey() {
        return bind.getValue().getKey();
    }

    public final boolean isEnabled() {
        return enabled.getValue();
    }

    public final boolean isHidden() {
        return !drawn.getValue();
    }

    public final boolean isPersistent() {
        return persistent;
    }

    public int getColor() {
        return color;
    }

    public final Category getCategory() {
        return category;
    }

    public final String getLabel() {
        return this.label;
    }

    public final void clearSuffix() {
        suffix = "";
    }

    public final void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public final String getSuffix() {
        if (suffix.length() == 0) {
            return "";
        }
        return " " + ChatFormatting.DARK_GRAY + "[" + ChatFormatting.WHITE + suffix + ChatFormatting.DARK_GRAY + "]";
    }

    @Override
    public boolean isListening() {
        return enabled.getValue() && mc.player != null;
    }

    public enum Category {
        COMBAT("Combat", 0xFFFF0000),
        EXPLOIT("Exploit", 0xFF7C00FF),
        MOVEMENT("Movement", 0xFF007CFF),
        PLAYER("Player",0xFF00FF00),
        VISUAL("Render",0xFFFFA200),
        MISC("Misc", 0xFF7C00FF),
        CLIENT("Other",0xA2A9CE);


        final int color;
        final String label;

        Category(String label, int color) {
            this.color = color;
            this.label = label;
        }

        public final int getColor() {
            return color;
        }

        public final String getLabel() {
            return label;
        }

        }
    }