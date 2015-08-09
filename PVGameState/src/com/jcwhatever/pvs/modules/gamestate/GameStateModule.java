package com.jcwhatever.pvs.modules.gamestate;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.gamestate.commands.GameStateCommand;

/**
 * Adds extension to allow setting player initial game state.
 */
public class GameStateModule extends PVStarModule {

    private static GameStateModule _module;

    public static GameStateModule getModule() {
        return _module;
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        _module = this;
        PVStarAPI.getExtensionManager().registerType(GameStateExtension.class);
        PVStarAPI.getCommandDispatcher().registerCommand(GameStateCommand.class);
    }
}
