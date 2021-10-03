package me.hollow.sputnik.client.modules.visual;

import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;

@ModuleManifest(label = "NoHurtCam", listen = false, category = Module.Category.VISUAL, color = 0x006600)
public class NoHurtCam extends Module {
    public static NoHurtCam INSTANCE;

    private final ICamera camera = new Frustum();
    private final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();

    public NoHurtCam() {
        INSTANCE = this;
    }


}
