package com.jcwhatever.bukkit.pvs.modules.party.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.player.PlayerHelper;
import com.jcwhatever.bukkit.pvs.modules.party.Party;
import com.jcwhatever.bukkit.pvs.modules.party.PartyManager;
import com.jcwhatever.bukkit.pvs.modules.party.PartyModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@ICommandInfo(
		parent="party",
		command="kick",
		staticParams={"playerName"},
		usage="/pv party kick <playerName>", 
		description="Kick a player from your party.",
		permissionDefault=PermissionDefault.TRUE)

public class KickSubCommand extends AbstractCommand {
	
	@Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getInstance().getManager();

		if (!manager.isInParty(p)) {
			tellError(p, "You don't have a party to kick anyone from.");
			return; // finish
		}
		
		Party party = manager.getParty(p);
		
		if (!party.getLeader().equals(p)) {
			tellError(p, "You can't kick players because you're not the party leader.");
			return; // finish
		}
		
		String playerName = args.getName("playerName");
		
		Player kickedPlayer = PlayerHelper.getPlayer(playerName);
		
		if (kickedPlayer == null) {
			tellError(p, "Could not find player '{0}'", playerName);
			return; // finish
		}
		
		if (!party.hasMember(kickedPlayer)) {
			tellError(p, "{0} is not in your party.", playerName);
			return; // finish
		}
		
		if (!manager.leaveParty(kickedPlayer)) {
			tellError(p, "Failed to kick player.");
			return; // finish
		}
		
		tell(kickedPlayer, "You've been kicked from {0}.", party.getPartyName());
		tellSuccess(p, "{0} has been kicked from your party.", kickedPlayer.getName());
    }
}


