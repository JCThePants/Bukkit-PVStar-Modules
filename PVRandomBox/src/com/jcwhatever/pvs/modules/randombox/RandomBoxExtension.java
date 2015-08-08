package com.jcwhatever.pvs.modules.randombox;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.managed.actionbar.ActionBars;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.managed.titles.Titles;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.events.ArenaPreStartEvent;
import com.jcwhatever.pvs.modules.randombox.events.RandomBoxOpenedEvent;
import com.jcwhatever.pvs.modules.randombox.events.RandomBoxPreOpenEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ArenaExtensionInfo(
        name = "PVRandomBox",
        description = "Add random box (chests) to an arena.")
public class RandomBoxExtension extends ArenaExtension implements IEventListener {

    private static Location CHEST_LOCATION = new Location(null, 0, 0, 0);

    private ArenaChests _chests;
    private ChestItems _items;
    private double _relocationChance = 0.1D;
    private int _xpLevelCost = 1;

    public ArenaChests getChests() {
        return _chests;
    }

    public ChestItems getItems() {
        return _items;
    }

    public double getRelocationChance() {
        return _relocationChance;
    }

    public void setRelocationChance(double chance) {
        _relocationChance = chance;

        IDataNode dataNode = getDataNode();
        dataNode.set("relocation-chance", chance);
        dataNode.save();
    }

    public int getXpLevelCost() {
        return _xpLevelCost;
    }

    public void setXpLevelCost(int cost) {
        _xpLevelCost = cost;

        IDataNode dataNode = getDataNode();
        dataNode.set("xp-level-cost", cost);
        dataNode.save();
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onEnable() {

        IDataNode dataNode = getDataNode();

        _relocationChance = dataNode.getDouble("relocation-chance", _relocationChance);
        _xpLevelCost = dataNode.getInteger("xp-level-cost", _xpLevelCost);

        _chests = new ArenaChests(getArena(), dataNode);
        _items = new ChestItems(dataNode.getNode("items"));

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onChestInteract(PlayerInteractEvent event) {

        if (!event.hasBlock())
            return;

        if (!getArena().getGame().isRunning())
            return;

        Block block = event.getClickedBlock();
        if (block.getType() != Material.CHEST)
            return;

        ArenaChests.ChestInfo info = _chests.getInfo(block.getLocation(CHEST_LOCATION));
        if (info == null)
            return;

        event.setCancelled(true);

        openRandomBox(event.getPlayer(), block);
    }

    @EventMethod(priority = EventSubscriberPriority.LAST)
    private void onArenaPreStart(@SuppressWarnings("unused") ArenaPreStartEvent event) {

        if (_chests.isRandomized())
            randomHideChests();
    }

    @EventMethod
    private void onArenaEnd(@SuppressWarnings("unused") ArenaEndedEvent event) {

        if (_chests.isRandomized())
            restoreChests();
    }

    private void openRandomBox(final Player player, Block block) {

        final IArenaPlayer arenaPlayer = PVStarAPI.getArenaPlayer(player);
        assert arenaPlayer != null;
        assert arenaPlayer.getArena() != null;

        RandomBoxPreOpenEvent event = new RandomBoxPreOpenEvent(arenaPlayer, block, _xpLevelCost);
        arenaPlayer.getArena().getEventManager().call(this, event);

        if (event.isCancelled())
            return;

        int cost = event.getExpCost();

        if (cost > 0) {

            if (cost > player.getLevel()) {
                ActionBars.create("{RED}Requires " + cost + " Exp Levels.").showTo(player);
                return;
            }

            int level = player.getLevel();

            player.setLevel(0);
            player.setLevel(level - cost);

            ActionBars.create("{YELLOW}" + cost + " Exp Levels deducted.").showTo(player);
        }

        Titles.create("{MAGIC}Selecting", 1, 80, 1).showTo(player);

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), 80, new Runnable() {
            @Override
            public void run() {
                showBoxView(arenaPlayer);
            }
        });
    }

    private void showBoxView(IArenaPlayer arenaPlayer) {

        assert arenaPlayer.getArena() != null;

        BoxView view = new BoxView(PVStarAPI.getPlugin(), this);
        ViewSession session = ViewSession.get(arenaPlayer.getPlayer(), null);
        session.next(view);

        RandomBoxOpenedEvent event = new RandomBoxOpenedEvent(arenaPlayer, view);
        arenaPlayer.getArena().getEventManager().call(this, event);
    }

    /**
     * Randomly remove chests from the arena.
     *
     * <p>The chests are restored when the arena ends.</p>
     */
    public void randomHideChests() {

        if (_chests.size() == 0)
            return;

        List<ArenaChests.ChestInfo> chestInfoList = _chests.getInfo();
        int maxChests = _chests.getMax();

        // no chests
        if (maxChests == 0) {
            for (ArenaChests.ChestInfo chestInfo : chestInfoList) {
                chestInfo.getLocation().getBlock().setType(Material.AIR);
            }
        }
        // chests limited
        else if (maxChests > 0) {// simple randomize

            Set<ArenaChests.ChestInfo> visible = new HashSet<>(chestInfoList.size());

            for (int i=0; i < maxChests; i++) {
                ArenaChests.ChestInfo chest = Rand.remove(chestInfoList);
                visible.add(chest);
            }

            for (ArenaChests.ChestInfo chestInfo : chestInfoList) {
                if (!visible.contains(chestInfo)) {
                    Chest chest = chestInfo.getChest();
                    if (chest == null)
                        continue;

                    chest.getInventory().clear();
                    chestInfo.getLocation().getBlock().setType(Material.AIR);
                }
            }
        }
        // any amount of chests
        else {

            for (ArenaChests.ChestInfo chestInfo : chestInfoList) {
                Chest chest = chestInfo.getChest();
                if (chest == null)
                    continue;

                // 1 in 4 chance of being removed
                if (Rand.getInt(3) == 0) {
                    chest.getInventory().clear();
                    chest.setType(Material.AIR);
                    chest.update(true);
                }
            }
        }
    }

    /**
     * Restore all chests in the arena.
     */
    public void restoreChests() {

        if (_chests.size() == 0)
            return;

        final Collection<ArenaChests.ChestInfo> chests = _chests.getInfo();

        for (ArenaChests.ChestInfo chestInfo : chests) {
            chestInfo.getLocation().getBlock().setType(Material.CHEST);
        }

        Scheduler.runTaskLater(PVStarAPI.getPlugin(), 1, new Runnable() {

            @Override
            public void run() {
                for (ArenaChests.ChestInfo chestInfo : chests) {
                    Chest chest = chestInfo.getChest();
                    if (chest == null)
                        continue;

                    if (chestInfo.getPresetContents() != null) {
                        chest.getInventory().setContents(chestInfo.getPresetContents().clone());
                    } else {
                        chest.getInventory().clear();
                    }

                    chest.update(true);
                }
            }
        });
    }
}
