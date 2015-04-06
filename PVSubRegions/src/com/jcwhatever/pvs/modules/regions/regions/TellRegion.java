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


package com.jcwhatever.pvs.modules.regions.regions;

import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.nucleus.utils.converters.Converters;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

@RegionTypeInfo(
        name="tell",
        description="Tell a player who enters or leaves the region a message.")
public class TellRegion extends AbstractPVRegion implements IEventListener {

    protected static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
            .set("max-triggers-per-player", PropertyValueType.INTEGER, 1,
                    "Set the max number of times the message will be displayed per player. -1 for unlimited.")

            .set("enter-message", PropertyValueType.STRING,
                    "The message to display to the player who enters the region.")

            .set("leave-message", PropertyValueType.STRING,
                    "The message to display to the player who enters the region.")

            .setConverters("enter-message", Converters.ALT_COLOR, Converters.DE_ALT_COLOR)
            .setConverters("leave-message", Converters.ALT_COLOR, Converters.DE_ALT_COLOR)

            .build()
        ;
    }

    private ElementCounter<UUID> _enterCount = new ElementCounter<UUID>(RemovalPolicy.BOTTOM_OUT);
    private ElementCounter<UUID> _leaveCount = new ElementCounter<UUID>(RemovalPolicy.BOTTOM_OUT);

    private int _maxTriggersPerPlayer = 1;
    private String _enterMessage;
    private String _leaveMessage;

    public TellRegion(String name) {
        super(name);
    }

    @Override
    protected boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {

        if (!isEnabled() || _enterMessage == null)
            return false;

        if (_maxTriggersPerPlayer > -1) {

            int tellCount = _enterCount.count(p.getUniqueId());

            if (tellCount >= _maxTriggersPerPlayer)
                return false;
        }

        return true;
    }

    @Override
    protected void onPlayerEnter(IArenaPlayer player, EnterRegionReason reason) {

        tellMessage(player, _enterMessage);

        _enterCount.add(player.getUniqueId());
    }


    @Override
    protected boolean canDoPlayerLeave(Player p, LeaveRegionReason reason) {

        if (!isEnabled() || _leaveMessage == null)
            return false;

        if (_maxTriggersPerPlayer > -1) {

            int tellCount = _leaveCount.count(p.getUniqueId());

            if (tellCount >= _maxTriggersPerPlayer)
                return false;
        }

        return true;
    }

    @Override
    protected void onPlayerLeave(IArenaPlayer player, LeaveRegionReason reason) {

        tellMessage(player, _leaveMessage);

        _leaveCount.add(player.getUniqueId());
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
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        setEventListener(false);
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _maxTriggersPerPlayer = dataNode.getInteger("max-triggers-per-player", _maxTriggersPerPlayer);
        _enterMessage = dataNode.getString("enter-message");
        _leaveMessage = dataNode.getString("leave-message");
    }

    @Nullable
    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return null;
    }

    protected void tellMessage(IArenaPlayer player, String message) {
        Msg.tell(player.getPlayer(), message);
    }

    @EventMethod
    private void onArenaEnd(ArenaEndedEvent event) {
        _enterCount.reset();
        _leaveCount.reset();
    }


}
