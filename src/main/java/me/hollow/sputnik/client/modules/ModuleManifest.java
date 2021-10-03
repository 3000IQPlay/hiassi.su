package me.hollow.sputnik.client.modules;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleManifest {
    String label() default "";
    Module.Category category();
    int key() default Keyboard.KEY_NONE;
    boolean persistent() default false;
    boolean drawn() default true;
    boolean listen() default true;
    int color() default -1 /* white */;
}
