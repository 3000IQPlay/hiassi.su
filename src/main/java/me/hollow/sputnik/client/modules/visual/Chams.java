package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

@ModuleManifest(label = "Chams", listen = false, category = Module.Category.VISUAL, color = 0xcc0099)
public final class Chams extends Module {

    private final Setting<SettingPage> page = register(new Setting<>("Page", SettingPage.OTHER));

    public final Setting<Boolean> chams = register(new Setting<>("Chams", false, v -> page.getValue() == SettingPage.OTHER));
    public final Setting<Boolean> wireframe = register(new Setting<>("Wireframe", false, v -> page.getValue() == SettingPage.OTHER));

    public final Setting<Float> scale = register(new Setting<>("Scale", 1.0f, 0.1f, 1.1f, v -> page.getValue() == SettingPage.OTHER));
    public final Setting<Float> lineWidth = register(new Setting<>("Linewidth", 1f, 0.1f, 3f, v -> page.getValue() == SettingPage.OTHER));

    public final Setting<Integer> alpha = register(new Setting<>("Alpha", 100, 0, 255, v -> page.getValue() == SettingPage.COLOR));
    public final Setting<Integer> visibleRed = register(new Setting<>("Visible Red", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));
    public final Setting<Integer> visibleGreen = register(new Setting<>("Visible Green", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));
    public final Setting<Integer> visibleBlue = register(new Setting<>("Visible Blue", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));

    public final Setting<Integer> invisibleRed = register(new Setting<>("Invisible Red", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));
    public final Setting<Integer> invisibleGreen = register(new Setting<>("Invisible Green", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));
    public final Setting<Integer> invisibleBlue = register(new Setting<>("Invisible Blue", 255, 0, 255, v -> page.getValue() == SettingPage.COLOR));


    public static Chams INSTANCE;

    public Chams() {
        INSTANCE = this;
    }

    public final void onRenderModel(ModelBase base, Entity entity, float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch, float scale) {
        if (entity instanceof EntityPlayer) {
            return;
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(lineWidth.getValue());
        GL11.glDepthMask(false);
        GL11.glColor4d(((float) Chams.INSTANCE.invisibleRed.getValue() / 255), ((float) Chams.INSTANCE.invisibleGreen.getValue() / 255), ((float) Chams.INSTANCE.invisibleBlue.getValue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
        base.render(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColor4d(((float) Chams.INSTANCE.visibleRed.getValue() / 255), ((float) Chams.INSTANCE.visibleGreen.getValue() / 255), ((float) Chams.INSTANCE.visibleBlue.getValue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
        base.render(entity, limbSwing, limbSwingAmount, age, headYaw, headPitch, scale);
        GL11.glEnable(3042);
        GL11.glPopAttrib();
    }

    public enum SettingPage {
        COLOR,
        OTHER
    }

}
