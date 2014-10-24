package com.jcwhatever.bukkit.pvs.modules.party.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.modules.party.Party;
import com.jcwhatever.bukkit.pvs.modules.party.PartyManager;
import com.jcwhatever.bukkit.pvs.modules.party.PartyModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@ICommandInfo(
		parent="party",
		command="leave",
		usage="/pv party leave", 
		description="Leave the party you're in.",
		permissionDefault=PermissionDefault.TRUE)

public class LeaveSubCommand extends AbstractCommand {
	
	@Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getInstance().getManager();

		if (!manager.isInParty(p)) {
			tellError(p, "You're not in a party.");
			return; // finish
		}
		
		Party party = manager.getParty(p);
		
		if (party.getLeader().equals(p)) {
			party.tell("{0} disbanded.", party.getPartyName());
			manager.disband(party);
			return; // finish
		}
		
		if (!manager.leaveParty(p)) {
			tellError(p, "Failed to leave party.");
			return; // finish
		}
		
		party.tell("{0} left the party.", p.getName());
		
		tellSuccess(p, "You've left {0}.", party.getPartyName());
    }


    
}

