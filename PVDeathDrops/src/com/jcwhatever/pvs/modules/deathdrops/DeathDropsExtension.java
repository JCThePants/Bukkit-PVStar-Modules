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


package com.jcwhatever.pvs.modules.deathdrops;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

@ArenaExtensionInfo(
        name="PVDeathDrops",
        description="Manage items dropped when players or mobs are killed in an arena.")
public class DeathDropsExtension extends ArenaExtension implements IEventListener {

    private Map<EntityType, DropSettings> _entitySettings = new EnumMap<>(EntityType.class);
    //private Map<UUID, PlayerStateSnapshot> _itemsToRestore = new PlayerMapHashMap<>(25);

    private DropSettings _globalSettings; // all
    private DropSettings _playerSettings; // player entity
    private DropSettings _mobSettings; // not a player entity but still a living entity
    private IDataNode _mobNode;
    private boolean _canKeepItemsOnDeath = false;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    public boolean canKeepItemsOnDeath() {
        return _canKeepItemsOnDeath;
    }

    public void setKeepItemsOnDeath(boolean canKeep) {
        _canKeepItemsOnDeath = canKeep;

        getDataNode().set("keep-items", canKeep);
        getDataNode().save();
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

    /*
     *  Player kills entity
     */
    @EventMethod
    private void onPlayerKillEntity(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        if (event.getEntity() instanceof Player)
            return;

        EntityType type = event.getEntity().getType();
        DropSettings settings = getLivingEntitySettings(type);
        if (settings == null)
            return;

        handleDeath(settings, event);
    }

    /*
     *  Player kills Player
     */
    @EventMethod
    private void onPlayerKillPlayer(PlayerDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        DropSettings settings = _playerSettings;
        if (settings == null)
            return;

        handleDeath(settings, event);
    }

    /*
     *  Player dies.
     */
    @EventMethod
    private void onPlayerDeath(PlayerDeathEvent event) {

        if (!_canKeepItemsOnDeath)
            return;

        event.setKeepInventory(true);
        event.getDrops().clear();
    }

    private void handleDeath(DropSettings settings, EntityDeathEvent event) {
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

    private void dropItems(EntityDeathEvent event, DropSettings settings) {

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

    private void addDrop(ItemStack item, EntityDeathEvent event, DropSettings settings) {

        if (settings.isDirectItemTransfer()) {
            // direct

            Player killer = event.getEntity().getKiller();

            killer.getInventory().addItem(item);
        }
        else {
            event.getDrops().add(item);
        }
    }

    private void dropExp(EntityDeathEvent event, DropSettings settings) {

        if (!Rand.chance((int)settings.getExpDropRate()))
            return;

        int dropAmount = settings.getExpDropAmount();

        if (settings.isDirectExpTransfer()) {
            event.getEntity().getKiller().giveExp(dropAmount);
        }
        else {
            event.setDroppedExp(dropAmount);
        }
    }

}
