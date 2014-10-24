package com.jcwhatever.bukkit.pvs.modules.party;

import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PartyManager {
	
	private Map<UUID, Party> _partyMap = new PlayerMap<Party>();
	
	private int _invitationTimeout = 20 * 30;
	
	public int getInvitationTimeout() {
		return _invitationTimeout / 20;
	}
	
	
	public void setInvitationTimeout(int seconds) {
		_invitationTimeout = seconds * 20;
	}
		
	public boolean isInParty(Player p) {
		PreCon.notNull(p);
		
		return _partyMap.containsKey(p.getUniqueId());
	}
	
	public boolean addPlayer(Player p, Party party) {
		PreCon.notNull(p);
		PreCon.notNull(party);
		
		if (isInParty(p))
			return false;
		
		List<Player> invitations = party.getInvitations();
		
		if (!invitations.contains(p))
			return false;
		
		invitations.remove(p);
		party._addMember(p);
		_partyMap.put(p.getUniqueId(), party);
		return true;
	}
	
	public boolean invitePlayer(Player p, Party party) {
		PreCon.notNull(p);
		PreCon.notNull(party);

		if (isInParty(p))
			return false;
		
		party._addInvitedPlayer(p, _invitationTimeout);
		
		return true;
	}
	
	public boolean leaveParty(Player p) {
		PreCon.notNull(p);
		
		if (!isInParty(p))
			return false;
		
		Party party = getParty(p);
		if (party.getLeader().equals(p)) {
			disband(party);
			return true;
		}
		
		party._removeMember(p);
		_partyMap.remove(p.getUniqueId());
		
		return true;
	}
	
	
	public boolean disband(Party party) {
		PreCon.notNull(party);
		
		for (Player p : party.getMembers()) {
			_partyMap.remove(p.getUniqueId());
		}
		
		party._disband();
				
		return true;
	}
	
	
	public Party getParty(Player p) {
		PreCon.notNull(p);
		
		Party party = _partyMap.get(p.getUniqueId());
		
		if (party == null) {
			party = new Party(p);
			_partyMap.put(p.getUniqueId(), party);
		}
		
		return party;
	}
	
	public List<Party> getParties() {
		HashSet<Party> set = new HashSet<Party>(_partyMap.values());
		return new ArrayList<Party>(set);
	}
	
	
	public List<Party> getInvitedParties(Player invitee) {
		List<Party> parties = getParties();
		List<Party> invited = new ArrayList<Party>(5);
		for (Party party : parties) {
			if (party.isInvited(invitee))
				invited.add(party);
		}
		
		return invited;
	}

}
