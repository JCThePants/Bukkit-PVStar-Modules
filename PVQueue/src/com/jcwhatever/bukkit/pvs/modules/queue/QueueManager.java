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


package com.jcwhatever.bukkit.pvs.modules.queue;

import com.jcwhatever.bukkit.generic.player.collections.PlayerQueue;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerJoinQueryEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class QueueManager {

    private static final String _META_PARTY_MEMBERS = "com.jcwhatever.bukkit.pvs.modules.queue.QueueManager._META_PARTY_MEMBERS";
    private static final String _META_QUEUED_ARENA = "com.jcwhatever.bukkit.pvs.modules.queue.QueueManager._META_QUEUED_ARENA";
    //private static Map<String, Arena> _queueMap;
    private static Map<Arena, QueueManager> _queueManagers = new HashMap<>(20);

    public static QueueManager get(Arena arena) {
        PreCon.notNull(arena);

        QueueManager queueManager = _queueManagers.get(arena);
        if (queueManager == null) {
            queueManager = new QueueManager(arena);
            _queueManagers.put(arena, queueManager);
        }

        return queueManager;
    }



	private final Arena _arena;
	private final Queue<Player> _queue;

	static {
		 //_queueMap = new PlayerMap<Arena>();
	}
	
	public QueueManager(Arena arena) {
		PreCon.notNull(arena);
				
		_arena = arena;
		_queue = new PlayerQueue();
	}

	public Arena getArena() {
		return _arena;
	}

	public boolean addNextPlayers() {

        if (!_arena.canJoin())
            return false;

		while (!_queue.isEmpty()) {
			Player p = _queue.peek();

            ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
			
			if (!p.isOnline()) {
				_queue.remove();
				continue;
			}

            // make sure other modules agree to the player joining the arena
            Set<ArenaPlayer> partyMembers = _arena.getEventManager().call(new PlayerJoinQueryEvent(_arena, player)).getPlayers();

            if (!partyMembers.contains(player)) {
                _queue.remove();
                // TODO tell player removed from queue
            }
            else {

                // make sure there is enough room for the party
                if (_arena.getAvailableSlots() < partyMembers.size()) {
                    player.getSessionMeta().set(_META_PARTY_MEMBERS, partyMembers);

                    for (ArenaPlayer partyMember : partyMembers) {
                        partyMember.getSessionMeta().set(_META_QUEUED_ARENA, _arena);
                    }

                    break;
                }

                _queue.remove(); // remove party leader/player from queue

                for (ArenaPlayer partyMember : partyMembers) {
                    _arena.join(partyMember, AddPlayerReason.PLAYER_JOIN);
                }
            }
        }

		return true;
	}
	
	public static Arena getCurrentQueue(ArenaPlayer player) {
		PreCon.notNull(player);

        return player.getSessionMeta().get(_META_QUEUED_ARENA);
	}
		
	public static boolean removePlayer(ArenaPlayer player) {
		PreCon.notNull(player);

        Arena arena = getCurrentQueue(player);
        if (arena == null)
            return false;

        QueueManager manager = get(arena);

		if (manager._queue.remove(player.getHandle())) {

            // remove player meta
            Set<ArenaPlayer> party = player.getSessionMeta().get(_META_PARTY_MEMBERS);
            player.getSessionMeta().set(_META_PARTY_MEMBERS, null);
            player.getSessionMeta().set(_META_QUEUED_ARENA, null);

            if (party != null) {
                for (ArenaPlayer partyMember : party) {
                    partyMember.getSessionMeta().set(_META_QUEUED_ARENA, null);
                }
            }
            return true;
        }

        return false;
	}
	
	public int getQueuePosition(ArenaPlayer player) {
		PreCon.notNull(player);

        Arena arena = getCurrentQueue(player);
        if (arena == null)
            return 0;

        Set<ArenaPlayer> party = player.getSessionMeta().get(_META_PARTY_MEMBERS);

		int inFront = 0;
		for (Player qp : _queue) {

            // check if player is in queue
			if (player.getUniqueId().equals(qp.getUniqueId()))
				return inFront + 1;

            // check if player is part of a queued players party
            if (party != null) {
                ArenaPlayer qPlayer = PVStarAPI.getArenaPlayer(qp);

                if (party.contains(qPlayer))
                    return inFront + 1;
            }

			inFront++;
		}
		
		return 0;
	}
	
	public boolean addPlayer(ArenaPlayer player) {
		PreCon.notNull(player);

        // make sure other modules agree to the player joining the arena
        Set<ArenaPlayer> partyMembers = _arena.getEventManager()
                .call(new PlayerJoinQueryEvent(_arena, player)).getPlayers();

        if (!partyMembers.contains(player))
            return false;

        if (!player.getHandle().isOnline())
            return false;

        // make sure player is removed from current queue
        removePlayer(player);

        // setup player meta
        player.getSessionMeta().set(_META_PARTY_MEMBERS, partyMembers);

        for (ArenaPlayer partyMember : partyMembers) {
            partyMember.getSessionMeta().set(_META_QUEUED_ARENA, _arena);
        }

		return _queue.add(player.getHandle());
	}

}
