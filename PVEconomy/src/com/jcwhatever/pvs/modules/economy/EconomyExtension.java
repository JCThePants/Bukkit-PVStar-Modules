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


package com.jcwhatever.pvs.modules.economy;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.providers.economy.Economy;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.events.players.PlayerAddedToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.pvs.api.events.players.PlayerWinEvent;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name="PVEconomy",
        description = "Adds economy earnings/rewards to an arena.")

public class EconomyExtension extends ArenaExtension implements IEventListener {

    private double _kill = 0.0D;
    private double _death = 0.0D;
    private double _participant = 0.0D;
    private double _win = 0.0D;
    private double _lose = 0.0D;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onAttach() {
        IDataNode settings = getDataNode();

        _kill = settings.getDouble("kill", _kill);
        _death = settings.getDouble("death", _death);
        _participant = settings.getDouble("participant", _participant);
        _win = settings.getDouble("win", _win);
        _lose = settings.getDouble("lose", _lose);
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onPlayerDeath(PlayerDeathEvent event) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

        if (player.getContext() != ArenaContext.GAME)
            return;

        // Death reward
        giveMoney(player, getDeathAmount());
    }

    @EventMethod
    private void onPlayerKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        IArenaPlayer killer = PVStarAPI.getArenaPlayer(event.getEntity().getKiller());

        if (killer.getContext() != ArenaContext.GAME)
            return;

        // Kill reward
        giveMoney(killer, getKillAmount());
    }

    @EventMethod
    private void onAddPlayer(PlayerAddedToContextEvent event) {
        if (event.getPlayer().getContext() == ArenaContext.GAME) {
            giveMoney(event.getPlayer(), getParticipantAmount());
        }
    }

    @EventMethod
    private void onPlayerWin(PlayerWinEvent event) {
        giveMoney(event.getPlayer(), getWinAmount());
    }

    @EventMethod
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
        getDataNode().save();
    }

    public void setLoseAmount(double amount) {
        _lose = amount;
        getDataNode().set("lose", amount);
        getDataNode().save();
    }

    public void setKillAmount(double amount) {
        _kill = amount;
        getDataNode().set("kill", amount);
        getDataNode().save();
    }

    public void setDeathAmount(double amount) {
        _death = amount;
        getDataNode().set("death", amount);
        getDataNode().save();
    }

    public void setParticipantAmount(double amount) {
        _participant = amount;
        getDataNode().set("participant", amount);
        getDataNode().save();
    }

    private void giveMoney(IArenaPlayer player, double amount) {
        PreCon.notNull(player);

        Economy.depositOrWithdraw(player.getUniqueId(), amount);
    }
}
