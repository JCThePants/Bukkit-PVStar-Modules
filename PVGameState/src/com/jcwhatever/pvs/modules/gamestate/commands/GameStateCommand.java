package com.jcwhatever.pvs.modules.gamestate.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.mixins.IVisibleCommand;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.gamestate.GameStateExtension;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="gamestate",
        description="Manage players initial game state in the selected arena. [PVGameState]")

public class GameStateCommand extends AbstractPVCommand implements IVisibleCommand {

    public GameStateCommand() {
        super();

        registerCommand(ExpLevelsSubCommand.class);
        registerCommand(HealthSubCommand.class);
        registerCommand(HungerSubCommand.class);
        registerCommand(KitSubCommand.class);
        registerCommand(MaxHealthSubCommand.class);
        registerCommand(SpeedSubCommand.class);
    }

    @Override
    public boolean isVisible(CommandSender sender) {
        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        return arena != null && arena.getExtensions().has(GameStateExtension.class);
    }
}