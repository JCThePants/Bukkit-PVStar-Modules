package com.jcwhatever.pvs.modules.randombox;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.randombox.commands.ChestsCommand;

/*
 * 
 */
public class RandomBoxModule extends PVStarModule {

    private static RandomBoxModule _module;

    public static RandomBoxModule getModule() {
        return _module;
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {
        _module = this;

        PVStarAPI.getExtensionManager().registerType(RandomBoxExtension.class);
        PVStarAPI.getCommandDispatcher().registerCommand(ChestsCommand.class);
    }
}
