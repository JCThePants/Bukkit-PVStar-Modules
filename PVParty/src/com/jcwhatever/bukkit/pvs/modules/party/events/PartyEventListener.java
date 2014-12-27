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


package com.jcwhatever.bukkit.pvs.modules.party.events;

import com.jcwhatever.nucleus.events.manager.NucleusEventHandler;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerJoinQueryEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerPreAddEvent;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.party.Party;
import com.jcwhatever.bukkit.pvs.modules.party.PartyManager;
import com.jcwhatever.bukkit.pvs.modules.party.PartyModule;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class PartyEventListener implements IEventListener {

    private static final String META_ALLOW_JOIN_ARENA =
            "com.jcwhatever.bukkit.pvs.modules.party.events.PartyEventListener.META_ALLOW_JOIN_ARENA";

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @NucleusEventHandler
    private void onPlayerJoinQuery(PlayerJoinQueryEvent event) {

        Set<ArenaPlayer> playersJoining = event.getPlayers();
        Iterator<ArenaPlayer> playerIterator = playersJoining.iterator();

        // Ensure party members are only allowed to join if
        // their party leader is in the collection.
        // Players not in a party are disregarded.
        while(playerIterator.hasNext()) {
            ArenaPlayer player = playerIterator.next();

            ArenaPlayer leader = getPartyLeader(player);
            if (leader == null)
                continue;

            if (!playersJoining.contains(leader)) {
                playerIterator.remove();
            }
            else {
                player.getSessionMeta().set(META_ALLOW_JOIN_ARENA, event.getArena());
            }
        }
    }


    // Check to see if player can be added
    @NucleusEventHandler
    private void onPlayerPreAdd(PlayerPreAddEvent event) {

        if (event.getReason() != AddPlayerReason.PLAYER_JOIN)
            return;

        if (!canJoin(event.getArena(), event.getPlayer(), true)) {
            event.setCancelled(true);
        }
    }


    // player has already been allowed to join, add party members if applicable.
    // handled separately in case another module cancels the PlayerPreAddEvent
    @NucleusEventHandler
    private void onPlayerAdded(PlayerAddedEvent event) {

        Player p = event.getPlayer().getPlayer();
        Arena arena = event.getArena();

        PartyManager manager = PartyModule.getModule().getManager();

        // Check Party Membership
        if (manager.isInParty(p)) {

            Party party = manager.getParty(p);
            if (party != null && !party.isDisbanded()) {

                // add party members
                List<Player> members = party.getMembers();
                for (Player member : members) {

                    if (member.equals(p)) // don't add player that is already joining
                        continue;

                    ArenaPlayer player = PVStarAPI.getArenaPlayer(member);

                    // don't add player that is already in arena
                    if (player.getArena() != null)
                        continue;

                    // add meta to allow player to join
                    player.getSessionMeta().set(META_ALLOW_JOIN_ARENA, event.getArena());

                    // join arena
                    arena.join(player);
                }
            }
        }
    }


    @Nullable
    private ArenaPlayer getPartyLeader(ArenaPlayer player) {

        Player p = player.getPlayer();

        PartyManager manager = PartyModule.getModule().getManager();

        // Check Party Membership
        if (!manager.isInParty(p))
            return null;

        Party party = manager.getParty(p);
        if (party == null || party.isDisbanded())
            return null;

        Player leader = party.getLeader();
        if (leader == null)
            return null;

        return PVStarAPI.getArenaPlayer(leader);
    }


    private boolean canJoin(Arena arena, ArenaPlayer player, boolean verbose) {

        if (arena.equals(player.getSessionMeta().get(META_ALLOW_JOIN_ARENA))) {
            return true;
        }

        Player p = player.getPlayer();

        PartyManager manager = PartyModule.getModule().getManager();

        // Check Party Membership
        if (manager.isInParty(p)) {

            Party party = manager.getParty(p);
            if (party != null && !party.isDisbanded()) {

                ArenaPlayer partyLeader = PVStarAPI.getArenaPlayer(party.getLeader());

                // only the party leader can join a game
                if (!party.getLeader().equals(p) && !arena.equals(partyLeader.getArena())) {

                    if (verbose)
                        Msg.tell(p, "{RED}Only the party leader can join a game. You are currently in {0}.", party.getPartyName());

                    return false;
                }

                // there must be enough room for the party
                if (arena.getSettings().getMaxPlayers() < party.size()) {

                    if (verbose)
                        Msg.tell(p, "{RED}The arena '{0}' does not have enough player slots for your party. Your parties size is {1} but the max players allowed is {2}.", arena.getName(), party.size(), arena.getSettings().getMaxPlayers());

                    return false;
                }

                if (arena.getAvailableSlots() < party.size()) {

                    if (verbose)
                        Msg.tell(p, "There is not enough room in arena '{0}' for your party at the moment. Try joining the queue by typeing '/{plugin-command} q {0}'.", arena.getName());

                    return false;
                }
            }
        }

        return true;
    }
}
