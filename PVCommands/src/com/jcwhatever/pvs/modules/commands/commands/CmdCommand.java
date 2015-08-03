package com.jcwhatever.pvs.modules.commands.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.mixins.IVisibleCommand;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.commands.CommandExtension;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="cmd",
        description="Manage commands allowed in the arena. [PVCommands]")

public class CmdCommand extends AbstractPVCommand implements IVisibleCommand {

    public CmdCommand() {
        super();

        registerCommand(AddSubCommand.class);
        registerCommand(DelSubCommand.class);
        registerCommand(ListSubCommand.class);
    }

    @Override
    public boolean isVisible(CommandSender sender) {
        IArena arena = PVStarAPI.getArenaManager().getSelectedArena(sender);
        return arena != null && arena.getExtensions().has(CommandExtension.class);
    }
}
