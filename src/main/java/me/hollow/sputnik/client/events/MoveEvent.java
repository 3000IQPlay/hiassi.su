package me.hollow.sputnik.client.events;


import tcb.bces.event.Event;

public final class MoveEvent extends Event {

    private double motionX, motionY, motionZ;

    public MoveEvent(double motionX, double motionY, double motionZ) {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public final double getMotionX() {
        return this.motionX;
    }
    public final double getMotionY() {
        return this.motionY;
    }
    public final double getMotionZ() {
        return this.motionZ;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }
}
