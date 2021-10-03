package me.hollow.sputnik.client.command.commands;

import me.hollow.sputnik.api.util.MessageUtil;
import me.hollow.sputnik.client.command.Command;
import me.hollow.sputnik.client.command.CommandManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.TutorialSteps;

@CommandManifest(label = "Tutorial", aliases = {"tut"})
public class TutorialCommand extends Command {

    @Override
    public void execute(String[] args) {
        Minecraft.getMinecraft().gameSettings.tutorialStep = TutorialSteps.NONE;
        Minecraft.getMinecraft().getTutorial().setStep(TutorialSteps.NONE);
        MessageUtil.sendClientMessage("Set tutorial step to none!", -11114);
    }

}
