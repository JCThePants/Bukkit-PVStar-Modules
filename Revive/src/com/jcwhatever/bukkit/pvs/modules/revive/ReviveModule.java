package com.jcwhatever.bukkit.pvs.modules.revive;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.revive.commands.ReviveCommand;

public class ReviveModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getCommandHandler().registerCommand(ReviveCommand.class);

        PVStarAPI.getExtensionManager().registerType(ReviveExtension.class);
    }
}
