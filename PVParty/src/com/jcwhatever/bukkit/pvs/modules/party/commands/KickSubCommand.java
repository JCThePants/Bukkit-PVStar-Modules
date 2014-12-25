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

import com.jcwhatever.generic.commands.AbstractCommand;
import com.jcwhatever.generic.commands.CommandInfo;
import com.jcwhatever.generic.commands.arguments.CommandArguments;
import com.jcwhatever.generic.commands.exceptions.CommandException;
import com.jcwhatever.generic.utils.player.PlayerUtils;
import com.jcwhatever.bukkit.pvs.modules.party.Party;
import com.jcwhatever.bukkit.pvs.modules.party.PartyManager;
import com.jcwhatever.bukkit.pvs.modules.party.PartyModule;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@CommandInfo(
		parent="party",
		command="kick",
		staticParams={"playerName"},
		description="Kick a player from your party.",
		permissionDefault=PermissionDefault.TRUE,

		paramDescriptions = {
				"playerName= The name of the player to kick."})

public class KickSubCommand extends AbstractCommand {
	
	@Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

		CommandException.assertNotConsole(this, sender);
		
		Player p = (Player)sender;

        PartyManager manager = PartyModule.getModule().getManager();

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
		
		Player kickedPlayer = PlayerUtils.getPlayer(playerName);
		
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


