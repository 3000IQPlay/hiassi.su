package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.client.*;
import me.hollow.sputnik.client.modules.combat.*;
import me.hollow.sputnik.client.modules.exploit.*;
import me.hollow.sputnik.client.modules.misc.*;
import me.hollow.sputnik.client.modules.movement.*;
import me.hollow.sputnik.client.modules.player.*;
import me.hollow.sputnik.client.modules.visual.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager(){}

    public void init() {
        register(new HUD());

        //CLIENT
        register(new ClickGui());
        register(new PopCounter());
        register(new MiddleClick());
        register(new Manage());
        register(new Colours());
        register(new FontModule());
        register(new DiscordRpc());

        //COMBAT
        register(new AutoArmor());
        register(new AutoCrystal());
        register(new AutoTrap());
        register(new Criticals());
        register(new HoleFiller());
        register(new Offhand());
        register(new Feetplace());
        register(new AntiRegear());

        //MISC
        register(new FakePlayer());
        register(new Velocity());
        register(new MultiTask());
        register(new NoRotate());
        register(new AutoRespawn());
        register(new NoFall());
        register(new AntiPush());
        register(new AutoFish());
        register(new AntiHunger());
        register(new GuiMove());
        register(new ChatSuffix());
        register(new GreenText());
        register(new ForceCrash());
        //register(new PluginsGrabber());
        register(new NoBreakAnimation());

        //MOVEMENT
        register(new ReverseStep());
        register(new Speed());
        register(new Step());
        register(new Strafe());
        register(new LiquidTweaks());
        register(new NoSlowDown());
        register(new ElytraFlight());
        register(new Sprint());

        //PLAYER
        register(new Interact());
        register(new Stacker());
        register(new AntiVoid());
        register(new PacketSpam());
        //register(new Heil());
        register(new Blink());

        //VISUAL
        register(new BlockHighlight());
        register(new EnchantColor());
        register(new EntityESP());
        register(new HoleESP());
        register(new Nametags());
        register(new SkyColour());
        register(new HeadRotations());
        register(new ViewmodelChanger());
        register(new ShulkerPreview());
        register(new VoidESP());
        register(new NoRender());
        register(new Skeleton());
        register(new LogOutSpots());
        register(new Chams());
        register(new TimeChanger());
        register(new Trajectories());
        register(new Tracers());
        register(new HitSphere());
        register(new NoHurtCam());
        register(new NoWeather());
        register(new Fullbright());

        //EXPLOIT
        register(new Burrow());
        register(new QueueSkip());
        register(new SpeedMine());
        register(new InstaMine());

        modules.forEach(Module::onLoad);
    }

    private void register(Module module) {
        this.modules.add(module);
    }

    public final List<Module> getModules() {
        return modules;
    }

    public final Module getModuleByClass(Class<?> clazz) {
        Module module = null;
        final int size = modules.size();
        for (int i = 0; i < size; ++i) {
            final Module m = modules.get(i);
            if (m.getClass() == clazz) {
                module = m;
            }
        }
        return module;
    }

    public final List<Module> getModulesByCategory(Module.Category category) {
        final List<Module> list = new ArrayList<>();
        for (final Module module : modules) {
            if (module.getCategory().equals(category)) {
                list.add(module);
            }
        }
        return list;
    }

    public final Module getModuleByLabel(String label) {
        Module module = null;
        for (final Module m : modules) {
            if (m.getLabel().equalsIgnoreCase(label)) {
                module = m;
            }
        }
        return module;
    }

}
