package me.hollow.sputnik.client.events;

public
class KeyEventN extends EventStage {

    public boolean info;
    public boolean pressed;

    public
    KeyEventN ( int stage , boolean info , boolean pressed ) {
        super ( stage );
        this.info = info;
        this.pressed = pressed;
    }
}
