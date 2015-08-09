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
        command="health",
        staticParams = { "amount="},
        description="Set or view initial player health amount in the selected arena.",

        paramDescriptions = {
                "amount= The amount of health the player starts the game with. 20 is normal max health."})

public class HealthSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _INFO =
            "Initial health in arena '{0: arena name}' is {1: amount}.";

    @Localizable static final String _SET =
            "Initial health in arena '{0: arena name}' changed to {1: amount}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, AbstractPVCommand.ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finished

        GameStateExtension extension = getExtension(sender, arena, GameStateExtension.class);
        if (extension == null)
            return; // finished

        if (args.isDefaultValue("amount")) {

            int amount = extension.getHealth();

            tell(sender, Lang.get(_INFO, arena.getName(), amount));
            return; // finished
        }

        int amount = args.getInteger("amount");

        extension.setHealth(amount);

        tellSuccess(sender, Lang.get(_SET, arena.getName(), amount));
    }
}

