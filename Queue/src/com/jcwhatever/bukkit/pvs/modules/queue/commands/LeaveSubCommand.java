package com.jcwhatever.bukkit.pvs.modules.queue.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.pvs.modules.queue.QueueManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@ICommandInfo(
        parent="queue",
        command={"leave"},
        usage="/{plugin-command} queue leave",
        description="Leave the queue.",
        permissionDefault= PermissionDefault.TRUE)

public class LeaveSubCommand extends AbstractPVCommand {

    @Localizable static final String _SUCCESS = "Left the queue for arena '{0}'.";
    @Localizable static final String _FAILED = "You're not in a queue.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);

        Player p = (Player)sender;
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        // check queue
        Arena arena = QueueManager.getCurrentQueue(player);
        if (arena == null) {
            tellError(p, Lang.get(_FAILED));
            return; // finish
        }

        QueueManager.removePlayer(player);
        tellSuccess(p, Lang.get(_SUCCESS, arena.getName()));
    }
}
