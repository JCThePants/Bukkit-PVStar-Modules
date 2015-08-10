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


package com.jcwhatever.pvs.modules.queue;

import com.jcwhatever.nucleus.collections.players.PlayerQueue;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.events.players.PlayerJoinQueryEvent;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nullable;

public final class QueueManager {

    private static final MetaKey<Set> META_PARTY_MEMBERS = new MetaKey<Set>(Set.class);
    private static final MetaKey<IArena> META_QUEUED_ARENA = new MetaKey<IArena>(IArena.class);
    //private static Map<String, Arena> _queueMap;
    private static Map<IArena, QueueManager> _queueManagers = new HashMap<>(20);

    public static QueueManager get(IArena arena) {
        PreCon.notNull(arena);

        QueueManager queueManager = _queueManagers.get(arena);
        if (queueManager == null) {
            queueManager = new QueueManager(arena);
            _queueManagers.put(arena, queueManager);
        }

        return queueManager;
    }



	private final IArena _arena;
	private final Queue<Player> _queue;

	static {
		 //_queueMap = new PlayerMap<Arena>();
	}
	
	public QueueManager(IArena arena) {
		PreCon.notNull(arena);
				
		_arena = arena;
		_queue = new PlayerQueue(PVStarAPI.getPlugin());
	}

	public IArena getArena() {
		return _arena;
	}

	public boolean addNextPlayers() {

        if (!_arena.canJoin())
            return false;

		while (!_queue.isEmpty()) {
			Player p = _queue.peek();

            IArenaPlayer player = PVStarAPI.getArenaPlayer(p);
			
			if (!p.isOnline()) {
				_queue.remove();
				continue;
			}

            // make sure other modules agree to the player joining the arena
            IArenaPlayerCollection partyMembers = _arena.getEventManager().call(this,
                    new PlayerJoinQueryEvent(_arena, player)).getPlayers();

            if (partyMembers.contains(player)) {

                // make sure there is enough room for the party
                if (_arena.getAvailableSlots() < partyMembers.size()) {
                    player.getSessionMeta().set(META_PARTY_MEMBERS, partyMembers);

                    for (IArenaPlayer partyMember : partyMembers) {
                        partyMember.getSessionMeta().setKey(META_QUEUED_ARENA, _arena);
                    }

                    break;
                }

                _queue.remove(); // remove party leader/player from queue

                for (IArenaPlayer partyMember : partyMembers) {
                    _arena.join(partyMember);
                }
            } else {
                _queue.remove();
                // TODO tell player removed from queue
            }
        }

		return true;
	}

    @Nullable
	public static IArena getCurrentQueue(IArenaPlayer player) {
		PreCon.notNull(player);

        return player.getSessionMeta().get(META_QUEUED_ARENA);
	}
		
	public static boolean removePlayer(IArenaPlayer player) {
		PreCon.notNull(player);

        IArena arena = getCurrentQueue(player);
        if (arena == null)
            return false;

        QueueManager manager = get(arena);

		if (manager._queue.remove(player.getPlayer())) {

            // remove player meta
            @SuppressWarnings("unchecked")
            Set<IArenaPlayer> party = player.getSessionMeta().get(META_PARTY_MEMBERS);

            player.getSessionMeta().setKey(META_PARTY_MEMBERS, null);
            player.getSessionMeta().setKey(META_QUEUED_ARENA, null);

            if (party != null) {
                for (IArenaPlayer partyMember : party) {
                    partyMember.getSessionMeta().setKey(META_QUEUED_ARENA, null);
                }
            }
            return true;
        }

        return false;
	}
	
	public int getQueuePosition(IArenaPlayer player) {
		PreCon.notNull(player);

        IArena arena = getCurrentQueue(player);
        if (arena == null)
            return 0;

        @SuppressWarnings("unchecked")
        Set<IArenaPlayer> party = player.getSessionMeta().get(META_PARTY_MEMBERS);

		int inFront = 0;
		for (Player qp : _queue) {

            // check if player is in queue
			if (player.getUniqueId().equals(qp.getUniqueId()))
				return inFront + 1;

            // check if player is part of a queued players party
            if (party != null) {
                IArenaPlayer qPlayer = PVStarAPI.getArenaPlayer(qp);

                if (party.contains(qPlayer))
                    return inFront + 1;
            }

			inFront++;
		}
		
		return 0;
	}
	
	public boolean addPlayer(IArenaPlayer player) {
		PreCon.notNull(player);

        // make sure other modules agree to the player joining the arena
        IArenaPlayerCollection partyMembers = _arena.getEventManager()
                .call(this, new PlayerJoinQueryEvent(_arena, player)).getPlayers();

        if (!partyMembers.contains(player))
            return false;

        if (!player.getPlayer().isOnline())
            return false;

        // make sure player is removed from current queue
        removePlayer(player);

        // setup player meta
        player.getSessionMeta().set(META_PARTY_MEMBERS, partyMembers);

        for (IArenaPlayer partyMember : partyMembers) {
            partyMember.getSessionMeta().setKey(META_QUEUED_ARENA, _arena);
        }

		return _queue.add(player.getPlayer());
	}
}
