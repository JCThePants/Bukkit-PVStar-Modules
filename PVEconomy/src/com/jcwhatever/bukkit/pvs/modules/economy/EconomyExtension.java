/* This file is part of PV-Star Modules: PVEconomy for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.economy;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaDeathEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerWinEvent;
import org.bukkit.event.entity.EntityDeathEvent;

@ArenaExtensionInfo(
        name="PVEconomy",
        description = "Adds economy earnings/rewards to an arena.")

public class EconomyExtension extends ArenaExtension implements GenericsEventListener {

    private double _kill = 0.0D;
    private double _death = 0.0D;
    private double _participant = 0.0D;
    private double _win = 0.0D;
    private double _lose = 0.0D;

    @Override
    protected void onEnable() {

        IDataNode settings = getDataNode();

        _kill = settings.getDouble("kill", _kill);
        _death = settings.getDouble("death", _death);
        _participant = settings.getDouble("participant", _participant);
        _win = settings.getDouble("win", _win);
        _lose = settings.getDouble("lose", _lose);

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onPlayerDeath(PlayerArenaDeathEvent event) {

        if (event.getPlayer().getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        // Death reward
        giveMoney(event.getPlayer(), getDeathAmount());
    }

    @GenericsEventHandler
    private void onPlayerKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        ArenaPlayer killer = PVStarAPI.getArenaPlayer(event.getEntity().getKiller());

        if (killer.getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        // Kill reward
        giveMoney(killer, getKillAmount());
    }

    @GenericsEventHandler
    private void onAddPlayer(PlayerAddedEvent event) {
        if (event.getPlayer().getArenaRelation() == ArenaPlayerRelation.GAME) {
            giveMoney(event.getPlayer(), getParticipantAmount());
        }
    }

    @GenericsEventHandler
    private void onPlayerWin(PlayerWinEvent event) {
        giveMoney(event.getPlayer(), getWinAmount());
    }

    @GenericsEventHandler
    private void onPlayerLose(PlayerLoseEvent event) {
        giveMoney(event.getPlayer(), getLoseAmount());
    }

    public double getKillAmount() {
        return _kill;
    }

    public double getDeathAmount() {
        return _death;
    }

    public double getParticipantAmount() {
        return _participant;
    }

    public double getWinAmount() {
        return _win;
    }

    public double getLoseAmount() {
        return _lose;
    }

    public void setWinAmount(double amount) {
        _win = amount;
        getDataNode().set("win", amount);
        getDataNode().saveAsync(null);
    }

    public void setLoseAmount(double amount) {
        _lose = amount;
        getDataNode().set("lose", amount);
        getDataNode().saveAsync(null);
    }

    public void setKillAmount(double amount) {
        _kill = amount;
        getDataNode().set("kill", amount);
        getDataNode().saveAsync(null);
    }

    public void setDeathAmount(double amount) {
        _death = amount;
        getDataNode().set("death", amount);
        getDataNode().saveAsync(null);
    }

    public void setParticipantAmount(double amount) {
        _participant = amount;
        getDataNode().set("participant", amount);
        getDataNode().saveAsync(null);
    }

    private void giveMoney(ArenaPlayer player, double amount) {
        PreCon.notNull(player);

        if (!isEnabled() || Double.compare(amount, 0.0D) == 0 || !EconomyHelper.hasEconomy())
            return;

        EconomyHelper.giveMoney(player.getHandle(), amount);
    }

}
