package com.jcwhatever.pvs.modules.commands;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.modules.commands.commands.CmdCommand;

/**
 * Manages whitelisted commands by adding an arena extension.
 */
public class CommandsModule extends PVStarModule {

    private static CommandsModule _module;

    public static CommandsModule getModule() {
        return _module;
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        _module = this;
        PVStarAPI.getExtensionManager().registerType(CommandExtension.class);
        PVStarAPI.getCommandDispatcher().registerCommand(CmdCommand.class);
    }
}
