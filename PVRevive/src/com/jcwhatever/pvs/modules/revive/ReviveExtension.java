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


package com.jcwhatever.pvs.modules.revive;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.managed.actionbar.ActionBars;
import com.jcwhatever.nucleus.managed.particles.Particles;
import com.jcwhatever.nucleus.managed.particles.types.IRedstoneDustParticle;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@ArenaExtensionInfo(
        name = "PVRevive",
        description = "Adds down and revive mechanics to an arena.")
public class ReviveExtension extends ArenaExtension implements IEventListener {

    private static final MetaKey<String> KEY_IS_DOWN = new MetaKey<>(String.class);
    private static final MetaKey<Player> KEY_KILLER = new MetaKey<>(Player.class);
    private static final MetaKey<IScheduledTask> KEY_TASK = new MetaKey<>(IScheduledTask.class);

    private int _timeToReviveSeconds = 20;
    private int _reviveHealth = 20;
    private ItemStack[] _revivalItems = new ItemStack[] { new ItemStack(Material.BLAZE_ROD) };

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    public int getTimeToReviveSeconds() {
        return _timeToReviveSeconds;
    }

    public void setTimeToReviveSeconds(int seconds) {
        _timeToReviveSeconds = seconds;

        getDataNode().set("time-to-revive", seconds);
        getDataNode().save();
    }

    public int getReviveHealth() {
        return _reviveHealth;
    }

    public void setReviveHealth(int reviveHealth) {
        _reviveHealth = reviveHealth;

        getDataNode().set("revive-health", reviveHealth);
        getDataNode().save();
    }

    public ItemStack[] getRevivalItems() {
        return _revivalItems;
    }

    public void setRevivalItems(ItemStack... items) {
        _revivalItems = items;

        getDataNode().set("revival-items", _revivalItems);
        getDataNode().save();
    }

    @Override
    protected void onEnable() {

        _timeToReviveSeconds = getDataNode().getInteger("time-to-revive", _timeToReviveSeconds);
        _reviveHealth = getDataNode().getInteger("revive-health", _reviveHealth);
        _revivalItems = getDataNode().getItemStacks("revival-items", _revivalItems);

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

        if (player.getArena() == null)
            return;

        if (player.getContext() != ArenaContext.GAME)
            return;

        // don't run revive if there are no other players to revive
        // the downed player.
        if (player.getArena().getGame().getPlayers().size() <= 1)
            return;

        MetaStore meta = player.getSessionMeta();

        if ("true".equals(meta.get(KEY_IS_DOWN))) {
            // allow death if player is down
            meta.setKey(KEY_IS_DOWN, null);
        }
        else {

            event.setDeathMessage(null);

            meta.setKey(KEY_IS_DOWN, "true");
            meta.setKey(KEY_KILLER, event.getEntity().getKiller());

            // cancel event
            double health = player.getPlayer().getHealth();
            if (Double.compare(health, 0.0D) == 0 || health < 0.0D)
                player.getPlayer().setHealth(1.0D);

            player.getPlayer().setHealth(_timeToReviveSeconds);

            player.setInvulnerable(true);
            player.setImmobilized(true);

            IScheduledTask task = Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 20, 20,
                    new PlayerDownedTimer(player));

            meta.setKey(KEY_TASK, task);

            IArena arena = player.getArena();
            if (arena != null) {

                arena.getGame().tell("{RED}{0} needs to be revived!", player.getName());

                String message = TextUtils.format("{RED}!!! {0} needs to be revived !!!", player.getName());
                ActionBars.create(message)
                        .showTo(arena.getGame().getPlayers().asPlayers());
            }
        }
    }

    @EventMethod(priority = EventSubscriberPriority.LAST, invokeForCancelled = true)
    private void onPlayerRevive(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());
        IArenaPlayer reviver = PVStarAPI.getArenaPlayer(event.getDamager());

        if (player.getContext() != ArenaContext.GAME)
            return;

        MetaStore meta = player.getSessionMeta();

        // make sure player is down
        if (!"true".equals(meta.get(KEY_IS_DOWN)))
            return;

        // only allow players on the same team to revive each other
        if (player.getTeam() != reviver.getTeam())
            return;

        // do no damage
        event.setDamage(0.0D);

        // get the item in the damager/reviver's hand
        ItemStack inHand = reviver.getPlayer().getItemInHand();

        // see if the item is a revival item
        for (ItemStack revivalItem : _revivalItems) {
            if (ItemStackMatcher.getDefault().isMatch(inHand, revivalItem)) {

                IScheduledTask task = player.getSessionMeta().get(KEY_TASK);
                if (task != null) {
                    task.cancel();
                }
                player.getPlayer().setHealth(_reviveHealth);
                break;
            }
        }
    }

    private static class PlayerDownedTimer extends TaskHandler {

        private static final IRedstoneDustParticle EFFECT;
        private static final Location PLAYER_LOCATION = new Location(null, 0, 0, 0);

        static {
            EFFECT = Particles.createRedstoneDust();
            EFFECT.setColor(Color.RED);
        }

        private IArenaPlayer _player;
        private double _health;

        public PlayerDownedTimer(IArenaPlayer player) {
            _player = player;
            _health = player.getPlayer().getHealth();
            _player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50000, 2));
        }

        @Override
        public void run() {
            _health -= 1.0D;

            if (_health <= 0.0D) {
                Player killer = _player.getSessionMeta().get(KEY_KILLER);
                if (killer != null) {
                    IArenaPlayer arenaKiller = PVStarAPI.getArenaPlayer(killer);
                    _player.kill(arenaKiller);
                }
                else {
                    _player.kill();
                }

                cancelTask();
                return;// finish
            }
            else if (Double.compare(_health, 4.0D) == 0) {
                _player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50000, 2));
            }

            _player.getPlayer().setHealth(_health);

            IArena arena = _player.getArena();
            if (arena != null) {
                EFFECT.showTo(arena.getGame().getPlayers().asPlayers(),
                        _player.getLocation(PLAYER_LOCATION).add(0, 2, 0), 1);
            }
        }

        @Override
        public void onCancel() {
            _player.getSessionMeta().setKey(KEY_IS_DOWN, null);
            _player.getSessionMeta().setKey(KEY_KILLER, null);
            _player.getSessionMeta().setKey(KEY_TASK, null);

            _player.getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
            _player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);

            _player.setInvulnerable(false);
            _player.setImmobilized(false);
        }
    }
}
