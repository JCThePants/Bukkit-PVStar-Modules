package com.jcwhatever.pvs.modules.gamestate.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.gamestate.GameStateExtension;
import com.jcwhatever.pvs.modules.gamestate.Lang;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="speed",
        staticParams = { "amount="},
        description="Set or view initial player walk speed in the selected arena.",

        paramDescriptions = {
                "amount= The walk speed the player starts the game with."})

public class SpeedSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _INFO =
            "Initial walk speed in arena '{0: arena name}' is {1: amount}.";

    @Localizable static final String _SET =
            "Initial walk speed in arena '{0: arena name}' changed to {1: amount}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, AbstractPVCommand.ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finished

        GameStateExtension extension = getExtension(sender, arena, GameStateExtension.class);
        if (extension == null)
            return; // finished

        if (args.isDefaultValue("amount")) {

            float amount = extension.getWalkSpeed();

            tell(sender, Lang.get(_INFO, arena.getName(), (int)amount));
            return; // finished
        }

        float amount = args.getFloat("amount");

        extension.setWalkSpeed(amount);

        tellSuccess(sender, Lang.get(_SET, arena.getName(), amount));
    }
}