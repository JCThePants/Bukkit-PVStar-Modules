package com.jcwhatever.bukkit.pvs.modules.deathdrops;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.ExpHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaRespawnEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ArenaExtensionInfo(
        name="PVDeathDrops",
        description="Manage items dropped when players or mobs are killed in an arena.")
public class DeathDropsExtension extends ArenaExtension implements GenericsEventListener {

    private Map<EntityType, DropSettings> _entitySettings = new EnumMap<>(EntityType.class);
    private Map<UUID, List<ItemStack>> _itemsToRestore = new HashMap<>(25);

    private DropSettings _globalSettings; // all
    private DropSettings _playerSettings; // player entity
    private DropSettings _mobSettings; // not a player entity but still a living entity
    private IDataNode _mobNode;
    private boolean _canKeepItemsOnDeath = false;

    public boolean canKeepItemsOnDeath() {
        return _canKeepItemsOnDeath;
    }

    public void setKeepItemsOnDeath(boolean canKeep) {
        _canKeepItemsOnDeath = canKeep;

        getDataNode().set("keep-items", canKeep);
        getDataNode().saveAsync(null);
    }

    public DropSettings getGlobalSettings() {
        return _globalSettings;
    }

    public DropSettings getPlayerSettings() {
        return _playerSettings;
    }

    public DropSettings getMobSettings() {
        return _mobSettings;
    }

    @Nullable
    public DropSettings getLivingEntitySettings(EntityType type) {
        PreCon.notNull(type);

        if (!type.isAlive())
            return null;

        DropSettings settings = _entitySettings.get(type);

        if (settings == null) {

            settings = new DropSettings(_mobSettings, _mobNode.getNode(type.name()));
            _entitySettings.put(type, settings);
        }

        return settings;
    }


    @Override
    protected void onEnable() {

        _canKeepItemsOnDeath = getDataNode().getBoolean("keep-items", _canKeepItemsOnDeath);

        _mobNode = getDataNode().getNode("mobs");

        _globalSettings = new DropSettings(null, getDataNode().getNode("global"));
        _playerSettings = new DropSettings(_globalSettings, getDataNode().getNode("player"));
        _mobSettings = new DropSettings(_globalSettings, _mobNode);

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onPlayerKill(PlayerArenaKillEvent event) {
        handleDeath(event);
        handleKill(event);
    }

    @GenericsEventHandler
    private void onPlayerRespawn(PlayerArenaRespawnEvent event) {
        if (!_canKeepItemsOnDeath)
            return;

        List<ItemStack> items = _itemsToRestore.get(event.getPlayer().getUniqueId());
        if (items == null)
            return;

        PlayerInventory inventory = event.getPlayer().getHandle().getInventory();

        for (ItemStack item : items) {
            inventory.addItem(item);
        }
    }

    @GenericsEventHandler
    private void  onArenaEnd(ArenaEndedEvent event) {
        _itemsToRestore.clear();
    }

    private void handleDeath(PlayerArenaKillEvent event) {
        if (event.getDeadPlayer() == null)
            return;

        if (!_canKeepItemsOnDeath)
            return;

        List<ItemStack> droppedItems = new ArrayList<>(event.getDrops());

        event.getDrops().clear();

        _itemsToRestore.put(event.getDeadPlayer().getUniqueId(), droppedItems);

    }

    private void handleKill(PlayerArenaKillEvent event) {
        DropSettings settings;

        // check for player kill
        if (event.getDeadPlayer() != null) {
            settings = _playerSettings;
        }
        // check for living entity kill
        else {
            EntityType type = event.getDeadEntity().getType();

            settings = getLivingEntitySettings(type);
        }

        if (settings == null)
            return;

        if (settings.isItemDropEnabled()) {
            dropItems(event, settings);
        }
        else {
            event.getDrops().clear();
        }

        if (settings.isExpDropEnabled()) {
            dropExp(event, settings);
        }
        else {
            event.setDroppedExp(0);
        }
    }

    private void dropItems(PlayerArenaKillEvent event, DropSettings settings) {

        if (!Rand.chance((int)settings.getItemDropRate()))
            return;

        ItemStack[] itemRewards = settings.getItemRewards();

        if (itemRewards.length == 0)
            return;

        // check if a random item should be dropped
        if (settings.isRandomItemDrop()) {

            ItemStack droppedItem = Rand.get(itemRewards);
            if (droppedItem != null && droppedItem.getType() != Material.AIR) {
                addDrop(droppedItem, event, settings);
            }
            return; // finished
        }

        // drop all items
        for (ItemStack reward : itemRewards) {
            if (reward == null || reward.getType() == Material.AIR)
                continue;

            // give
            addDrop(reward.clone(), event, settings);
        }
    }

    private void addDrop(ItemStack item, PlayerArenaKillEvent event, DropSettings settings) {

        if (settings.isDirectItemTransfer()) {
            // direct
            event.getPlayer().getHandle().getInventory().addItem(item);
        }
        else {
            event.getDrops().add(item);
        }
    }

    private void dropExp(PlayerArenaKillEvent event, DropSettings settings) {

        if (!Rand.chance((int)settings.getExpDropRate()))
            return;

        int dropAmount = settings.getExpDropAmount();

        if (settings.isDirectExpTransfer()) {
            ExpHelper.incrementExp(event.getPlayer().getHandle(), dropAmount);
        }
        else {
            event.setDroppedExp(dropAmount);
        }
    }

}
