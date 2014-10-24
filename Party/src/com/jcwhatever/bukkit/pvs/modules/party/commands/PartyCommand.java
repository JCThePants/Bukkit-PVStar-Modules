package com.jcwhatever.bukkit.pvs.modules.party.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.party.Party;
import com.jcwhatever.bukkit.pvs.modules.party.PartyManager;
import com.jcwhatever.bukkit.pvs.modules.party.PartyModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;


@ICommandInfo(
		command={"party", "p"},
		staticParams={
		        "page=1"
		},
		usage="/{plugin-command} party",
		description="Party management.",
		permissionDefault=PermissionDefault.TRUE)

public class PartyCommand extends AbstractCommand {
	
	public PartyCommand() {
		super();
		
		registerSubCommand(InviteSubCommand.class);
		registerSubCommand(JoinSubCommand.class);
		registerSubCommand(KickSubCommand.class);
		registerSubCommand(NewSubCommand.class);
	}

	@Override
	public void execute(CommandSender sender, CommandArguments args)
	        throws InvalidValueException, InvalidCommandSenderException {

	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);
	    
	    int page = args.getInt("page");

		Player p = (Player)sender;
        PartyManager manager = PartyModule.getInstance().getManager();
				
		if (!manager.isInParty(p)) {
		    String message = Lang.get("No information to display. You're not in a party.");
			tellError(p, message);
			return; // finish
		}
		
		Party party = manager.getParty(p);
		
		if (party.isDisbanded())
			return; // finish
		
		ChatPaginator pagin = Msg.getPaginator(party.getPartyName());
		
		List<Player> members = party.getMembers();
		List<Player> invitees = party.getInvitations();
		
		String label = null;
		String noneLabel = Lang.get("<none>");
		
		// Leader
		label = Lang.get("Leader");
		pagin.add(label, party.getLeader().getName());
		
		
		// Members
		List<String> names = new ArrayList<String>(members.size());
		for (Player member : members) {
			if (!party.getLeader().equals(member))
				names.add(member.getName());
		}
		
		label = Lang.get("Members");
		pagin.add(label, names.size() > 0 ? TextUtils.concat(names, ", ") : noneLabel);
		
		
		// Invitations
		List<String> invited = new ArrayList<String>(invitees.size());
		for (Player invitee : invitees) {
			invited.add(invitee.getName());
		}
		
		label = Lang.get("Invitations");
		pagin.add(label, invited.size() > 0 ? TextUtils.concat(invited, ", ") : noneLabel);
		
		pagin.show(sender, page, FormatTemplate.DEFINITION);
	}
}