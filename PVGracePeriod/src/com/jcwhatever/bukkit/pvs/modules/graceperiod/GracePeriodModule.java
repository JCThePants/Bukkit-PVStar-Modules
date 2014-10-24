package com.jcwhatever.bukkit.pvs.modules.graceperiod;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.graceperiod.commands.GraceCommand;

public class GracePeriodModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getExtensionManager().registerType(GracePeriodExtension.class);
    }

    @Override
    protected void onEnable() {

        AbstractCommand command = PVStarAPI.getCommandHandler().getCommand("game");
        if (command != null) {
            command.registerSubCommand(GraceCommand.class);
        }
        else {
            PVStarAPI.getCommandHandler().registerCommand(GraceCommand.class);
        }
    }
}
