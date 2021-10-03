package me.hollow.sputnik.client.modules.visual;

import com.google.common.base.Strings;
import me.hollow.sputnik.Main;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.ColorUtil;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.ConnectionEvent;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.bces.listener.Subscribe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ModuleManifest(label = "Logouts", category = Module.Category.VISUAL, color = 0x99ff33)
public class LogOutSpots extends Module {

    private final Setting<Float> range = register(new Setting("Range", 300.0f, 50.0f, 500.0f));
    private final Setting<Integer> red = register(new Setting("Red", 255, 0, 255));
    private final Setting<Integer> green = register(new Setting("Green", 0, 0, 255));
    private final Setting<Integer> blue = register(new Setting("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = register(new Setting("Alpha", 255, 0, 255));
    private final Setting<Boolean> scaleing = register(new Setting("Scale", false));
    private final Setting<Float> scaling = register(new Setting("Size", 4.0f, 0.1f, 20.0f));
    private final Setting<Float> factor = register(new Setting("Factor", 0.3f, 0.1f, 1.0f, v -> scaleing.getValue()));
    private final Setting<Boolean> smartScale = register(new Setting("SmartScale", false, v -> scaleing.getValue()));
    private final Setting<Boolean> rect = register(new Setting("Rectangle", true));

    private final List<LogoutPos> spots = new ArrayList<>();
    final AccessorRenderManager renderManager = (AccessorRenderManager) mc.getRenderManager();


    @Override
    public void onDisconnect() {
        if (mc.player == null || mc.world == null) return;
        spots.clear();
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;
        spots.clear();
    }

    public AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(
                bb.minX - mc.getRenderManager().viewerPosX,
                bb.minY - mc.getRenderManager().viewerPosY,
                bb.minZ - mc.getRenderManager().viewerPosZ,
                bb.maxX - mc.getRenderManager().viewerPosX,
                bb.maxY - mc.getRenderManager().viewerPosY,
                bb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    @Override
    public void onRender3D() {
        if (spots.isEmpty()) {
            return;
        }

        List<LogoutPos> syncSpots;
        synchronized (spots) {
            syncSpots = new ArrayList<>(spots);
        }

        for (final LogoutPos spot : syncSpots) {
            if (spot.getEntity() != null) {
                final AxisAlignedBB bb = interpolateAxis(spot.getEntity().getEntityBoundingBox());
                RenderUtil.drawBoundingBox(bb, new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()), 1.0f);
                double x = interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, mc.getRenderPartialTicks()) - renderManager.getRenderPosX();
                double y = interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, mc.getRenderPartialTicks()) - renderManager.getRenderPosY();
                double z = interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, mc.getRenderPartialTicks()) - renderManager.getRenderPosZ();
                renderNameTag(spot.getName(), x, y, z, mc.getRenderPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent tickEvent) {
        if (mc.player == null || mc.world == null) return;
        float range = this.range.getValue() * this.range.getValue();
        spots.removeIf(spot -> mc.player.getDistanceSq(spot.getEntity()) >= range);
    }

    @Subscribe
    public void onConnection(ConnectionEvent event) {
        if (mc.player == null || mc.world == null) return;
        if(event.getStage() == 0) {
            spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        } else if(event.getStage() == 1) {
            EntityPlayer entity = event.getEntity();
            UUID uuid = event.getUuid();
            String name = event.getName();
            if(name != null && entity != null && uuid != null) {
                spots.add(new LogoutPos(name, uuid, entity));
            }
        }
    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7D;
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = mc.fontRenderer.getStringWidth(displayTag) >> 1;
        double scale = (0.0018 + scaling.getValue() * (distance * factor.getValue())) / 1000.0;

        if (distance <= 8 && smartScale.getValue()) {
            scale = 0.0245D;
        }

        if(!scaleing.getValue()) {
            scale = scaling.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        GlStateManager.enableBlend();
        if(rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(mc.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1.5F, 0x55000000);
        }
        GlStateManager.disableBlend();

        mc.fontRenderer.drawStringWithShadow(displayTag, -width, -(mc.fontRenderer.FONT_HEIGHT - 1), ColorUtil.toRGBA(new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue())));

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

    private static class LogoutPos {

        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }


        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public EntityPlayer getEntity() {
            return entity;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction())) {
                return;
            }

            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null)
                    .forEach(data -> {
                        final UUID id = data.getProfile().getId();
                        switch(packet.getAction()) {
                            case ADD_PLAYER:
                                String name = data.getProfile().getName();
                                Main.INSTANCE.getBus().post(new ConnectionEvent(0, id, name));
                                break;
                            case REMOVE_PLAYER:
                                EntityPlayer entity = mc.world.getPlayerEntityByUUID(id);
                                if(entity != null) {
                                    String logoutName = entity.getName();
                                    Main.INSTANCE.getBus().post(new ConnectionEvent(1, entity, id, logoutName));
                                } else {
                                    Main.INSTANCE.getBus().post(new ConnectionEvent(2, id, null));
                                }
                                break;
                        }
                    });
        }
    }

}
