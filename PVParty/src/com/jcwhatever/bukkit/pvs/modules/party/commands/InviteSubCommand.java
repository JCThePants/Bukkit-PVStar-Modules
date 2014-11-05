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
		command="invite",
		staticParams={"playerName"},
		usage="/pv party invite <playerName>", 
		description="Invite a player to your party.",
		permissionDefault=PermissionDefault.TRUE)

public class InviteSubCommand extends AbstractCommand {
	
	@Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {
        
	    InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getModule().getManager();

		if (!manager.isInParty(p)) {
			tellError(p, "You don't have a party to invite anyone to.");
			return; // finish
		}
		
		Party party = manager.getParty(p);
		
		if (!party.getLeader().equals(p)) {
			tellError(p, "You can't invite players because you're not the party leader.");
			return; // finish
		}
		
		String playerName = args.getName("playerName");
		
		Player invitee = PlayerHelper.getPlayer(playerName);
		
		if (invitee == null) {
			tellError(p, "Could not find player '{0}'", playerName);
			return; // finish
		}
		
		if (!manager.invitePlayer(invitee, party)) {
			tellError(p, "Failed to invite player.");
			return; // finish
		}
		
		
		if (manager.getInvitedParties(invitee).size() > 1)
			tell(invitee, "You've been invited to {0}. Type '/pv party join {1}' to join.", party.getPartyName(), party.getLeader().getName());
		else
			tell(invitee, "You've been invited to {0}. Type '/pv party join' to join.", party.getPartyName());
				
		tell(invitee, "Invitation expires after {0} seconds.", manager.getInvitationTimeout());
		
		tellSuccess(p, "{0} has been invited to your party.", invitee.getName());
    }

}

