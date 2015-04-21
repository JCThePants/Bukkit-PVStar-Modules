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


package com.jcwhatever.pvs.modules.startcountdown;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;
import com.jcwhatever.nucleus.managed.titles.Titles;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.context.IGameContext;
import com.jcwhatever.pvs.api.arena.options.ArenaStartReason;
import com.jcwhatever.pvs.api.events.ArenaPreStartEvent;
import com.jcwhatever.pvs.api.events.players.PlayerJoinedArenaEvent;
import com.jcwhatever.pvs.api.utils.Msg;

import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name="PVStartCountdown",
        description = "Adds a countdown timer before the game starts.")
public class StartCountdownExtension extends ArenaExtension implements IEventListener {

    @Localizable static final String _AUTO_START_INFO =
            "{YELLOW}Countdown to start will begin once {0} or more players " +
                    "have joined. Type '/pv vote' if you would like to start the countdown now. All players " +
                    "must vote in order to start the countdown early.";
    @Localizable static final String _STARTING_COUNTDOWN = "{YELLOW}Starting in {0} seconds...";
    @Localizable static final String _MOD_10_SECONDS = "{YELLOW}{0} seconds...";
    @Localizable static final String _SECONDS = "{RED}{0}...";
    @Localizable static final String _GO = "{GREEN}Go!";

    private int _startCountdown = 10; // setting

    private IScheduledTask _countdownTask;
    private boolean _isStarting;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    /*
     * Determine if the countdown till the next game is running.
     */
    public boolean isCountdownRunning() {
        return _countdownTask != null && !_countdownTask.isCancelled();
    }

    /*
     * Cancel the countdown.
     */
    public void cancelCountdown() {
        if (!isCountdownRunning())
            return;

        _countdownTask.cancel();
    }

    /*
     * Get the number of seconds to countdown from
     * before the game starts.
     */
    public int getStartCountdownSeconds() {
        return _startCountdown;
    }

    /*
     * Set the number of seconds to countdown from
     * before the game starts.
     */
    public void setStartCountdownSeconds(int seconds) {
        _startCountdown = seconds;

        getDataNode().set("start-countdown", seconds);
        getDataNode().save();
    }

    @Override
    protected void onAttach() {
        _startCountdown = getDataNode().getInteger("start-countdown", _startCountdown);
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
    private void onPlayerJoin(PlayerJoinedArenaEvent event) {

        Msg.tell(event.getPlayer(), Lang.get(_AUTO_START_INFO,
                getArena().getLobby().getSettings().getMinAutoStartPlayers()));
    }

    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onArenaPreStart(ArenaPreStartEvent event) {

        // prevent start during countdown
        if (!_isStarting) {
            event.setCancelled(true);

            if (!isCountdownRunning()) {
                startCountdown(event.getReason(), event.getJoiningPlayers());
            }
        }
    }

    /*
     * Begin the game start countdown.
     */
    private void startCountdown(final ArenaStartReason reason, IArenaPlayerCollection players) {

        final IGameContext gameManager = getArena().getGame();

        // make sure countdown isn't already running and
        // the game isn't in progress.
        if (isCountdownRunning() || gameManager.isRunning())
            return;

        // don't start if there are no players
        if (players == null || players.isEmpty())
            return;

        // tell players the countdown is starting.
        Titles.showTo(players.asPlayers(), Lang.get(_STARTING_COUNTDOWN, getStartCountdownSeconds()));

        // schedule countdown task
        _countdownTask = Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 20, 20, new TaskHandler() {

            private int elapsedSeconds = 0;

            @Override
            public void run() {

                elapsedSeconds++;

                long remaining = getStartCountdownSeconds() - elapsedSeconds;

                IArenaPlayerCollection group = reason == ArenaStartReason.AUTO
                        ? getArena().getLobby().getNextGroup()
                        : getArena().getLobby().getReadyGroup();

                // cancel countdown if there is no longer a group of players to start the game
                if (group.isEmpty()) {
                    cancelTask();
                }
                // cancel countdown task once countdown is completed.
                else if (remaining <= 0) {
                    _isStarting = true;

                    getArena().getGame().start(reason);

                    Titles.showTo(group.asPlayers(), Lang.get(_GO));

                    cancelTask();

                    _isStarting = false;
                }
                // tell current time left at 10 seconds intervals
                else if (remaining > 5) {

                    if (remaining % 10 == 0) {
                        Titles.showTo(group.asPlayers(), Lang.get(_MOD_10_SECONDS, remaining));
                    }

                }
                // tell current time left at 1 seconds intervals
                else {
                    Titles.showTo(group.asPlayers(), Lang.get(_SECONDS, remaining));
                }
            }

        });
    }

}
