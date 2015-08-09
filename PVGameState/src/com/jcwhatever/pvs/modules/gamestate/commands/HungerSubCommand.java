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
        command="hunger",
        staticParams = { "amount="},
        description="Set or view initial player hunger amount in the selected arena.",

        paramDescriptions = {
                "amount= The amount of hunger the player starts the game with."})

public class HungerSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _INFO =
            "Initial hunger in arena '{0: arena name}' is {1: amount}.";

    @Localizable static final String _SET =
            "Initial hunger in arena '{0: arena name}' changed to {1: amount}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, AbstractPVCommand.ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finished

        GameStateExtension extension = getExtension(sender, arena, GameStateExtension.class);
        if (extension == null)
            return; // finished

        if (args.isDefaultValue("amount")) {

            int amount = extension.getHunger();

            tell(sender, Lang.get(_INFO, arena.getName(), amount));
            return; // finished
        }

        int amount = args.getInteger("amount");

        extension.setHunger(amount);

        tellSuccess(sender, Lang.get(_SET, arena.getName(), amount));
    }
}

