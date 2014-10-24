package com.jcwhatever.bukkit.pvs.modules.revive;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.utils.Scheduler.TaskHandler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.PlayerMeta;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaDeathEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerDamagedEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@ArenaExtensionInfo(
        name = "PVRevive",
        description = "Adds down and revive mechanics to an arena.")
public class ReviveExtension extends ArenaExtension implements GenericsEventListener {

    private static final String KEY_IS_DOWN = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_IS_DOWN";
    private static final String KEY_KILLER = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_KILLER";
    private static final String KEY_TASK = "com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension.KEY_TASK";

    private int _timeToReviveSeconds = 20;
    private int _reviveHealth = 20;
    private ItemStack[] _revivalItems = new ItemStack[] { new ItemStack(Material.BLAZE_ROD) };

    public int getTimeToReviveSeconds() {
        return _timeToReviveSeconds;
    }

    public void setTimeToReviveSeconds(int seconds) {
        _timeToReviveSeconds = seconds;

        getDataNode().set("time-to-revive", seconds);
        getDataNode().saveAsync(null);
    }

    public int getReviveHealth() {
        return _reviveHealth;
    }

    public void setReviveHealth(int reviveHealth) {
        _reviveHealth = reviveHealth;

        getDataNode().set("revive-health", reviveHealth);
        getDataNode().saveAsync(null);
    }

    public ItemStack[] getRevivalItems() {
        return _revivalItems;
    }

    public void setRevivalItems(ItemStack[] items) {
        _revivalItems = items;

        getDataNode().set("revival-items", items);
        getDataNode().saveAsync(null);
    }

    @Override
    protected void onEnable() {

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    private void loadSettings() {
        _timeToReviveSeconds = getDataNode().getInteger("time-to-revive", _timeToReviveSeconds);
        _reviveHealth = getDataNode().getInteger("revive-health", _reviveHealth);
        _revivalItems = getDataNode().getItemStacks("revival-items", _revivalItems);
    }

    @GenericsEventHandler(priority = GenericsEventPriority.FIRST)
    private void onPlayerDeath(PlayerArenaDeathEvent event) {

        if (event.getPlayer().getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        PlayerMeta meta = event.getPlayer().getSessionMeta();

        if ("true".equals(meta.get(KEY_IS_DOWN))) {
            // allow death if player is down
            meta.set(KEY_IS_DOWN, null);
        }
        else {
            meta.set(KEY_IS_DOWN, "true");
            meta.set(KEY_KILLER, event.getKiller());

            event.setCancelled(true);

            Player p = event.getPlayer().getHandle();
            p.setHealth(_timeToReviveSeconds);

            event.getPlayer().setInvulnerable(true);
            event.getPlayer().setImmobilized(true);

            ScheduledTask task = Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 20, 20, new PlayerDownedTimer(event.getPlayer()));
            meta.set(KEY_TASK, task);
        }
    }

    @GenericsEventHandler(priority = GenericsEventPriority.WATCHER)
    private void onPlayerRevive(PlayerDamagedEvent event) {

        if (event.getPlayer().getArenaRelation() != ArenaPlayerRelation.GAME)
            return;

        PlayerMeta meta = event.getPlayer().getSessionMeta();

        // make sure player is down
        if (!"true".equals(meta.get(KEY_IS_DOWN)))
            return;

        // check that there is a player attempting to revive the player
        ArenaPlayer damager = event.getDamagerPlayer();
        if (damager == null)
            return;

        // only allow players on the same team to revive each other
        if (damager.getTeam() != event.getPlayer().getTeam())
            return;

        // get the item in the damager/reviver's hand
        ItemStack inHand = damager.getHandle().getItemInHand();

        // see if the item is a revival item
        for (ItemStack revivalItem : _revivalItems) {
            if (ItemStackComparer.getDefault().isSame(inHand, revivalItem)) {

                ScheduledTask task = event.getPlayer().getSessionMeta().get(KEY_TASK);
                if (task != null) {
                    task.cancel();
                }
                event.getPlayer().getHandle().setHealth(_reviveHealth);
                break;
            }
        }

    }

    private static class PlayerDownedTimer extends TaskHandler {

        private ArenaPlayer _player;
        private double _health;

        public PlayerDownedTimer(ArenaPlayer player) {
            _player = player;
            _health = player.getHandle().getHealth();
            _player.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 50000, 2));
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
                _player.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50000, 2));
            }

            _player.getHandle().setHealth(_health);
        }

        @Override
        public void onCancel() {
            _player.getSessionMeta().set(KEY_IS_DOWN, null);
            _player.getSessionMeta().set(KEY_KILLER, null);
            _player.getSessionMeta().set(KEY_TASK, null);

            _player.getHandle().removePotionEffect(PotionEffectType.CONFUSION);
            _player.getHandle().removePotionEffect(PotionEffectType.BLINDNESS);

            _player.setInvulnerable(false);
            _player.setImmobilized(false);
        }
    }

}
