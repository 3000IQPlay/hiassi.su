package me.hollow.sputnik;

import me.hollow.sputnik.client.managers.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import tcb.bces.bus.DRCEventBus;
import me.hollow.sputnik.api.util.IconUtil;
import me.hollow.sputnik.client.managers.HWIDManager;
import me.hollow.sputnik.api.util.*;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mod(name = "hiassi.su", modid = "hiassi.su", version = Main.VERSION)
public final class Main {

    public static final String NAME = "hiassi.su";
    public static final String VERSION = "0.1";
    public static final Logger logger = LogManager.getLogger(NAME);

    private final DRCEventBus eventBus = new DRCEventBus();

    private final File directory = new File(Minecraft.getMinecraft().gameDir, "hiassi.su");

    private final CommandManager commandManager = new CommandManager();
    private final ModuleManager moduleManager = new ModuleManager();
    private final ConfigManager configManager = new ConfigManager();
    private final FriendManager friendManager = new FriendManager();
    private final EventManager eventManager = new EventManager();
    private final FileManager fileManager = new FileManager();
    private final SafeManager safeManager = new SafeManager();
    private final PopManager popManager = new PopManager();
    private final TPSManager tpsManager = new TPSManager();


    public static final Main INSTANCE = new Main();

    public static final FontManager fontManager = new FontManager();

    public final void init() {
        moduleManager.init();
        commandManager.init();
        eventManager.init();
        friendManager.setDirectory(new File(directory, "friends.json"));
        friendManager.init();
        tpsManager.init();
        popManager.init();
        configManager.init();
        eventBus.bind();
        Display.setTitle("hiassi.su 0.1");
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());
    }

    public static void setWindowIcon() {
        if (Util.getOSType() != Util.EnumOS.OSX) {
            try (InputStream inputStream16x = Minecraft.class.getResourceAsStream("/assets/hiassi.su/icons/icon-16x.png");
                 InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/hiassi.su/icons/icon-32x.png")) {
                ByteBuffer[] icons = new ByteBuffer[]{IconUtil.INSTANCE.readImageToBuffer(inputStream16x), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};
                Display.setIcon(icons);
            } catch (Exception e) {
                logger.debug("unable to do the shit");
            }
        }
    }


    public EventManager getEventManager() {
        return eventManager;
    }

    public final TPSManager getTpsManager() {
        return tpsManager;
    }

    public final DRCEventBus getBus() {
        return eventBus;
    }

    public final PopManager getPopManager() {
        return popManager;
    }

    public final FriendManager getFriendManager() {
        return friendManager;
    }

    public final ConfigManager getConfigManager() {
        return configManager;
    }

    public final ModuleManager getModuleManager() {
        return moduleManager;
    }

    public final SafeManager getSafeManager() {
        return safeManager;
    }


    final static class ShutdownThread extends Thread {

        @Override
        public void run() {
            logger.info("Trying to save config....");
            Main.INSTANCE.getConfigManager().saveConfig(Main.INSTANCE.getConfigManager().config.replaceFirst("hiassi.su/", ""));
            Main.INSTANCE.getFriendManager().unload();
            logger.info("Config saved!");
        }

        @Mod.EventHandler
        public void init(FMLInitializationEvent event) {

        }
    }
}
