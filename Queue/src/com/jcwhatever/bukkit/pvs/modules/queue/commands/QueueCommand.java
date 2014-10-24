package com.jcwhatever.bukkit.pvs.modules.queue.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.pvs.modules.queue.QueueManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@ICommandInfo(
        command={"q", "queue"},
        staticParams={"arenaName=$info"},
        usage="/{plugin-command} q [arenaName]",
        description="Queue to join the specified arena as soon as it's available or get your current queue status.",
        permissionDefault= PermissionDefault.TRUE)

public class QueueCommand extends AbstractPVCommand {

    @Localizable static final String _NOT_IN_QUEUE = "You are not in a queue.";
    @Localizable static final String _QUEUE_INFO = "You are queued for arena '{0}'. Your queue position is {1}.";
    @Localizable static final String _QUEUE_LEAVE = "Type '/{plugin-command} q leave' if you wish to leave the queue.";
    @Localizable static final String _CANT_JOIN_IN_GAME = "You cannot join a queue while you are in a game.";
    @Localizable static final String _ARENA_DISABLED = "Arena '{0}' is not enabled.";
    @Localizable static final String _ARENA_NOT_FOUND = "An arena named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Joined queue for arena '{0}'. Your queue position is {1}.";
    @Localizable static final String _FAILED = "Failed to join queue.";

    public QueueCommand() {
        super();

        registerSubCommand(LeaveSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);

        Player p = (Player)sender;
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        // get info about players current queue status.
        if (args.getString("arenaName").equals("$info")) {

            Arena arena = QueueManager.getCurrentQueue(player);
            if (arena == null) {
                tell(p, Lang.get(_NOT_IN_QUEUE));
            }
            else {
                QueueManager manager = QueueManager.get(arena);
                tell(p, Lang.get(_QUEUE_INFO, arena.getName(), manager.getQueuePosition(player)));
                tell(p, Lang.get(_QUEUE_LEAVE));
            }

            return; // finish
        }

        // Make sure the player is not already in an arena
        Arena currentArena = player.getArena();
        if (currentArena != null) {
            tellError(p, Lang.get(_CANT_JOIN_IN_GAME));
            return; // finish
        }

        String arenaName = args.getName("arenaName");

        Arena arena = getArena(sender, arenaName);
        if (arena == null)
            return; // finish


        if (!arena.getSettings().isVisible()) {
            tellError(p, Lang.get(_ARENA_NOT_FOUND, arenaName));
            return; // finish
        }

        if (!arena.getSettings().isEnabled()) {
            tellError(p, Lang.get(_ARENA_DISABLED, arenaName));
            return; // finish
        }

        if (arena.canJoin()) {
            arena.join(player, AddPlayerReason.PLAYER_JOIN);
            return; // finish
        }

        QueueManager manager = QueueManager.get(arena);
        if (manager.addPlayer(player)) {
            tellSuccess(p, Lang.get(_SUCCESS, arena.getName(), manager.getQueuePosition(player)));
        }
        else {
            tellError(p, Lang.get(_FAILED));
        }
    }
}
