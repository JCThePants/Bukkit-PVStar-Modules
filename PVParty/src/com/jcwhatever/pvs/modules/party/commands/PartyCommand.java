/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.pvs.modules.party.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.party.Lang;
import com.jcwhatever.pvs.modules.party.Party;
import com.jcwhatever.pvs.modules.party.PartyManager;
import com.jcwhatever.pvs.modules.party.PartyModule;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;


@CommandInfo(
		command={"party", "p"},
		staticParams={ "page=1" },
		description="Party management.",
		permissionDefault=PermissionDefault.TRUE,

		paramDescriptions = {
				"page= {PAGE}"})

public class PartyCommand extends AbstractCommand implements IExecutableCommand {
	
	public PartyCommand() {
		super();
		
		registerCommand(InviteSubCommand.class);
		registerCommand(JoinSubCommand.class);
		registerCommand(KickSubCommand.class);
		registerCommand(NewSubCommand.class);
	}

	@Override
	public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

		CommandException.checkNotConsole(getPlugin(), this, sender);
	    
	    int page = args.getInteger("page");

		Player p = (Player)sender;
        PartyManager manager = PartyModule.getModule().getManager();
				
		if (!manager.isInParty(p))
			throw new CommandException("No information to display. You're not in a party.");

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
		
		pagin.show(sender, page, FormatTemplate.CONSTANT_DEFINITION);
	}
}