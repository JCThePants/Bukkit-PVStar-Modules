package com.jcwhatever.bukkit.pvs.modules.economy;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.economy.commands.EconomyCommand;

public class EconomyModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getExtensionManager().registerType(EconomyExtension.class);
        PVStarAPI.getCommandHandler().registerCommand(EconomyCommand.class);
    }

}
