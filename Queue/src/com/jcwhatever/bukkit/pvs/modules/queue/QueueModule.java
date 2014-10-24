package com.jcwhatever.bukkit.pvs.modules.queue;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.queue.commands.QueueCommand;

public class QueueModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getCommandHandler().registerCommand(QueueCommand.class);
    }
}
