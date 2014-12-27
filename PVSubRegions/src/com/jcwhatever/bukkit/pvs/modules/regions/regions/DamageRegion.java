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


package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

import org.bukkit.entity.Player;

import java.util.Map;

@RegionTypeInfo(
        name="damage",
        description="Players are damaged (or given health) when they enter the region.")
public class DamageRegion extends AbstractPVRegion {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("damage", PropertyValueType.DOUBLE, 1.0D,
                        "The amount of damage inflicted on a player. Negative values give health.")
                .buildDefinitions()
        ;
    }

    private double _damage = 1.0D;

    public DamageRegion(String name) {
        super(name);
    }


    @Override
    protected void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason) {

        Player p = player.getPlayer();

        if (_damage < 0) {
            double health = p.getHealth() + Math.abs(_damage);
            health = Math.min(p.getMaxHealth(), health);

            p.setHealth(health);
        }
        else {
            p.damage(_damage);
        }
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        return false;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        setEventListener(true);
    }

    @Override
    protected void onDisable() {
        setEventListener(false);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _damage = dataNode.getDouble("damage", _damage);
    }

    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }
}
