package com.jcwhatever.bukkit.pvs.modules.graceperiod;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.managers.GameManager;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerDamagedEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;

@ArenaExtensionInfo(
        name="PVGracePeriod",
        description = "Add grace period to the beginning of an arena which prevents PVP.")
public class GracePeriodExtension extends ArenaExtension implements GenericsEventListener {

    private int _gracePeriodSeconds = 10;

    public boolean _isGracePeriod = false;

    /**
     * Get grace period time in seconds.
     */
    public int getGracePeriodSeconds() {
        return _gracePeriodSeconds;
    }

    /**
     * Set grace period time.
     */
    public void setGracePeriodSeconds(int seconds) {
        _gracePeriodSeconds = seconds;

        getDataNode().set("seconds", seconds);
        getDataNode().saveAsync(null);
    }


    @Override
    protected void onEnable() {

        _gracePeriodSeconds = getDataNode().getInteger("seconds", _gracePeriodSeconds);

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onArenaStart(ArenaStartedEvent event) {

        GameManager gameManager = getArena().getGameManager();

        if (gameManager.getSettings().isPvpEnabled() ||
            gameManager.getSettings().isTeamPvpEnabled()) {

            gameManager.tell("Pvp grace period for the next {0} seconds.", _gracePeriodSeconds);

            ArenaScheduler.runTaskLater(getArena(), 20 * _gracePeriodSeconds,
                    new GracePeriod());
        }
    }

    @GenericsEventHandler(priority = GenericsEventPriority.FIRST)
    private void onPvp(PlayerDamagedEvent event) {

        if (!_isGracePeriod)
            return;

        if (event.getDamagerPlayer() == null)
            return;

        if (event.getPlayer().getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        event.setCancelled(true);
    }

    private class GracePeriod implements Runnable {

        @Override
        public void run() {
            _isGracePeriod = false;
            getArena().getGameManager().tell("Pvp grace period ended.");
        }
    }
}
