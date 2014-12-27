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


package com.jcwhatever.bukkit.pvs.modules.party;

import com.jcwhatever.nucleus.collections.timed.TimedArrayList;
import com.jcwhatever.nucleus.collections.players.PlayerSet;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class Party {

	Set<Player> _players = new PlayerSet(PVStarAPI.getPlugin());
	TimedArrayList<Player> _invitations = new TimedArrayList<Player>();
    WeakReference<Player> _leader;

	private String _partyName;

	Party (Player leader) {
		_players.add(leader);
		_leader = new WeakReference<Player>(leader);
				
		_partyName = leader.getName();
		
		if (_partyName.charAt(_partyName.length() - 1) != 's')
			_partyName += "'s";
		
		_partyName += " Party";
		
	}

	@Nullable
	public Player getLeader() {
		if (_leader == null)
			return null;
		
		return _leader.get();
	}
	
	public boolean hasMember(Player p) {
		return _players.contains(p);
	}
	
	public boolean isInvited(Player p) {
		return _invitations.contains(p);
	}
	
	public List<Player> getMembers() {
		return new ArrayList<Player>(_players);
	}
	
	public List<Player> getInvitations() {
		return new ArrayList<Player>(_invitations);
	}
		
	public String getPartyName() {
		return _partyName;
	}
	
	public int size() {
		return _players.size();
	}
	
	public boolean isDisbanded() {
		return _leader == null || _leader.get() == null;
	}
	
	public void tell(String msg, Object... params) {
		for (Player p : _players) {
			Msg.tell(p, msg, params);
		}
	}
	
	public void _addMember(Player p) {
		_players.add(p);
	}

	public void _addInvitedPlayer(Player p, int invitationTimeout) {
		_invitations.add(p, invitationTimeout);
	}

	public void _removeMember(Player p) {
		_players.remove(p);		
	}

	public void _disband() {
		_leader = null;
		_players.clear();		
	}
}
