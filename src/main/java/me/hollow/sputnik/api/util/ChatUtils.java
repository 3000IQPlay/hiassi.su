package me.hollow.sputnik.api.util;

import me.hollow.sputnik.Main;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatUtils{

    public static void component(ITextComponent component)
    {
        if(Wrapper.INSTANCE.player() == null) {
            return;
        } else {
            Wrapper.INSTANCE.mc().ingameGUI.getChatGUI();
        }
        Wrapper.INSTANCE.mc().ingameGUI.getChatGUI()
                .printChatMessage(new TextComponentTranslation("")
                        .appendSibling(component));
    }

    public static void message(String message)
    {
        component(new TextComponentTranslation("\u00a78" + Main.NAME + "\u00a77 " + message));
    }

    public static void warning(String message)
    {
        message("\u00a78[\u00a7eWARNING\u00a78]\u00a7e " + message);
    }

    public static void error(String message)
    {
        message("\u00a78[\u00a74ERROR\u00a78]\u00a7c " + message);
    }
}
