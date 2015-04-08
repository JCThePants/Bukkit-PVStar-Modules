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
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.modules.party.Party;
import com.jcwhatever.pvs.modules.party.PartyManager;
import com.jcwhatever.pvs.modules.party.PartyModule;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
		parent="party",
		command="join",
		staticParams={"playerName=$default"},
		description="Join a party you've been invited to.",
		permissionDefault=PermissionDefault.TRUE,

		paramDescriptions = {
				"playerName= The name of the player who owns the party to join. Only required if " +
						"invited to more than 1 party."})

public class JoinSubCommand extends AbstractCommand implements IExecutableCommand {

	@Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

		CommandException.checkNotConsole(getPlugin(), this, sender);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getModule().getManager();
		
		if (manager.isInParty(p)) {
			tellError(p, "You're already in a party. You must leave the party you're in before you can join another.");
			return; // finish
		}
		
		String playerName = args.getString("playerName");
		List<Party> invited = manager.getInvitedParties(p);
		
		if (invited.size() == 0) {
			tellError(p, "You haven't been invited to any parties.");
			return; // finish
		}
		
		if (playerName.equals("$default") && invited.size() > 1) {
			
			tellError(p, "You've been invited to multiple parties. Please specify which player party you want to join. Type '/pv party join ?' for help.");
			
			List<String> leaderNames = new ArrayList<String>(invited.size());
			for (Party party : invited) {
				leaderNames.add(party.getLeader().getName());
			}
			
			tell(p, "You've received invitations from: {0}", TextUtils.concat(leaderNames, ", "));			
			
			return; // finish
		}
		
		if (playerName.equals("$default") && invited.size() == 1) {
			Party party = invited.get(0);
			if (!manager.addPlayer(p, party)) {
				tellError(p, "Failed to join party.");
				return; // finish
			}
			party.tell("{0} has joined the party.", p.getName());
			return; // finish
		}
		
		
		
		
		Player leader = PlayerUtils.getPlayer(playerName);
		if (leader == null) {
			tellError(p, "Player '{0}' was not found.", playerName);
			return; // finish
		}
		
		if (!manager.isInParty(leader)) {
			tellError(p, "{0} is not the leader of a party.", leader.getName());
			return; // finish
		}
		
		Party party = manager.getParty(leader);
		
		if (!party.getLeader().equals(leader)) {
			tellError(p, "{0} is not the leader of {1}.", leader.getName(), party.getPartyName());
			return; // finish
		}
		
		if (!party.isInvited(p)) {
			tellError(p, "You're not invited to {0} or you're invitation has expired.", party.getPartyName());
			return; // finish
		}
				
		if (!manager.addPlayer(p, party)) {
			tellError(p, "Failed to join party.");
			return; // finish
		}
		
		party.tell("{0} has joined the party.", p.getName());
    }
}


