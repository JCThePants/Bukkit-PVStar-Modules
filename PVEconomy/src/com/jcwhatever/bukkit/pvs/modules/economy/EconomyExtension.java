package com.jcwhatever.bukkit.pvs.modules.economy;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaDeathEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerWinEvent;

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
    private void onPlayerKill(PlayerArenaKillEvent event) {

        if (event.getPlayer().getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        // Kill reward
        giveMoney(event.getPlayer(), getKillAmount());
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
