package com.jcwhatever.bukkit.pvs.modules.deathdrops;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.DropsCommand;

public class DeathDropsModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getExtensionManager().registerType(DeathDropsExtension.class);
        PVStarAPI.getCommandHandler().registerCommand(DropsCommand.class);
    }

}
