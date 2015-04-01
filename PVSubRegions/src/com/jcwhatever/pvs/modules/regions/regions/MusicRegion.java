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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.sounds.types.ResourceSound;
import com.jcwhatever.nucleus.sounds.SoundSettings;
import com.jcwhatever.nucleus.sounds.playlist.PlayList.PlayerSoundQueue;
import com.jcwhatever.nucleus.sounds.playlist.SimplePlayList;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RegionTypeInfo(
        name="music",
        description="Trigger music for players entering the region.")
public class MusicRegion extends AbstractPVRegion {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("resource-sound", PropertyValueType.STRING,
                        "Set the sound that is played.")

                .set("loop", PropertyValueType.BOOLEAN,
                        "Set play sounds on a loop.")
                .build()
        ;
    }

    private SimplePlayList _playList;
    private boolean _isLoop;

    public MusicRegion(String name) {
        super(name);
    }

    @Override
    public boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {
        return _playList != null && _playList.size() != 0 && super.canDoPlayerEnter(p, reason);
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason) {

        PlayerSoundQueue currentQueue = _playList.getSoundQueue(player.getPlayer());
        if (currentQueue != null)
            return;

        _playList.addPlayer(player.getPlayer(), new SoundSettings().addLocations(getCenter()));
    }

    @Override
    public boolean canDoPlayerLeave(Player p, LeaveRegionReason reason) {
        return _playList != null;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
        _playList.removePlayer(player.getPlayer());
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

        _isLoop = dataNode.getBoolean("loop", _isLoop);

        String rawSounds = dataNode.getString("resource-sound");

        if (rawSounds == null)
            return;

        String[] soundNames = TextUtils.PATTERN_COMMA.split(rawSounds);
        List<ResourceSound> sounds = new ArrayList<>(soundNames.length);

        for (String soundName : soundNames) {

            ResourceSound sound = Nucleus.getSoundManager().getSound(soundName.trim());
            if (sound == null) {
                Msg.debug("Sound '{0}' not found while loading musical region '{1}'.", soundName, getName());
                continue;
            }

            sounds.add(sound);
        }

        _playList = new SimplePlayList(PVStarAPI.getPlugin(), sounds);
    }

    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }
}
