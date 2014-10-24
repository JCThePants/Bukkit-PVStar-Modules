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
		command="new",
		usage="/pv party new", 
		description="Create a new party.",
		permissionDefault=PermissionDefault.TRUE)

public class NewSubCommand extends AbstractCommand {
	
	@Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getInstance().getManager();
		
		if (manager.isInParty(p)) {
			Party current = manager.getParty(p);
			tellError(p, "You can't create a new party until you leave the one you're in. You're currently in {0}.", current.getPartyName());
			return; // finish
		}
		
		Party party = manager.getParty(p); // creates new party with player as leader
		
		tellSuccess(p, "{0} created. Type '/pv party invite ?' to find out how to invite players to your party.", party.getPartyName());
    }

    
}



