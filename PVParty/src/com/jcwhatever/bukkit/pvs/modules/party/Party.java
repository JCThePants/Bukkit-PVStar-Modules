package com.jcwhatever.bukkit.pvs.modules.party;

import com.jcwhatever.bukkit.generic.collections.TimedList;
import com.jcwhatever.bukkit.generic.player.collections.PlayerSet;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Party {

	Set<Player> _players = new PlayerSet();
	TimedList<Player> _invitations = new TimedList<Player>();
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
