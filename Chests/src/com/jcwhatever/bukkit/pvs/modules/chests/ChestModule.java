package com.jcwhatever.bukkit.pvs.modules.chests;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.chests.commands.ChestsCommand;

public class ChestModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        PVStarAPI.getExtensionManager().registerType(ChestExtension.class);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getCommandHandler().registerCommand(ChestsCommand.class);

    }
}
