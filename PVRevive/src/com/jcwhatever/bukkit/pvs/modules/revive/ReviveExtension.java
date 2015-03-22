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


package com.jcwhatever.bukkit.pvs.modules.revive;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.PlayerMeta;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.nucleus.utils.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.utils.scheduler.TaskHandler;

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

    private static final String KEY_IS_DOWN = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_IS_DOWN";
    private static final String KEY_KILLER = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_KILLER";
    private static final String KEY_TASK = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_TASK";

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

    public void setRevivalItems(ItemStack[] items) {
        _revivalItems = items;

        getDataNode().set("revival-items", items);
        getDataNode().save();
    }

    @Override
    protected void onAttach() {

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onRemove() {

        getArena().getEventManager().unregister(this);
    }

    private void loadSettings() {
        _timeToReviveSeconds = getDataNode().getInteger("time-to-revive", _timeToReviveSeconds);
        _reviveHealth = getDataNode().getInteger("revive-health", _reviveHealth);
        _revivalItems = getDataNode().getItemStacks("revival-items", _revivalItems);
    }

    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onPlayerDeath(PlayerDeathEvent event) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

        if (player.getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        PlayerMeta meta = player.getSessionMeta();

        if ("true".equals(meta.get(KEY_IS_DOWN))) {
            // allow death if player is down
            meta.set(KEY_IS_DOWN, null);
        }
        else {

            meta.set(KEY_IS_DOWN, "true");
            meta.set(KEY_KILLER, event.getEntity().getKiller());

            // cancel event
            double health = player.getPlayer().getHealth();
            if (Double.compare(health, 0.0D) == 0 || health < 0.0D)
                player.getPlayer().setHealth(1.0D);

            player.getPlayer().setHealth(_timeToReviveSeconds);

            player.setInvulnerable(true);
            player.setImmobilized(true);

            IScheduledTask task = Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 20, 20,
                    new PlayerDownedTimer(player));

            meta.set(KEY_TASK, task);
        }
    }

    @EventMethod(priority = EventSubscriberPriority.WATCHER)
    private void onPlayerRevive(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        if (!(event.getDamager() instanceof Player))
            return;

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());
        ArenaPlayer reviver = PVStarAPI.getArenaPlayer(event.getDamager());

        if (player.getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        PlayerMeta meta = player.getSessionMeta();

        // make sure player is down
        if (!"true".equals(meta.get(KEY_IS_DOWN)))
            return;

        // only allow players on the same team to revive each other
        if (player.getTeam() != reviver.getTeam())
            return;

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

        private ArenaPlayer _player;
        private double _health;

        public PlayerDownedTimer(ArenaPlayer player) {
            _player = player;
            _health = player.getPlayer().getHealth();
            _player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50000, 2));
        }

        @Override
        public void run() {
            _health -= 1.0D;

            if (_health <= 0.0D) {
                ArenaPlayer killer = _player.getSessionMeta().get(KEY_KILLER);

                _player.kill(killer);

                cancelTask();
                return;// finish
            }
            else if (Double.compare(_health, 4.0D) == 0) {
                _player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50000, 2));
            }

            _player.getPlayer().setHealth(_health);
        }

        @Override
        public void onCancel() {
            _player.getSessionMeta().set(KEY_IS_DOWN, null);
            _player.getSessionMeta().set(KEY_KILLER, null);
            _player.getSessionMeta().set(KEY_TASK, null);

            _player.getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
            _player.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);

            _player.setInvulnerable(false);
            _player.setImmobilized(false);
        }
    }

}
