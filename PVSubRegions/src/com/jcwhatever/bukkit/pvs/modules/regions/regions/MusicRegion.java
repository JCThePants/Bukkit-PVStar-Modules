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

import com.jcwhatever.bukkit.generic.sounds.PlayList;
import com.jcwhatever.bukkit.generic.sounds.PlayList.PlayerSoundQueue;
import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import com.jcwhatever.bukkit.generic.sounds.SoundManager;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RegionTypeInfo(
        name="music",
        description="Trigger music for players entering the region.")
public class MusicRegion extends AbstractPVRegion {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("resource-sound", ValueType.STRING, "Set the sound that is played.")
                .set("loop", ValueType.BOOLEAN, "Set play sounds on a loop.")
        ;
    }

    private PlayList _playList;
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

        PlayerSoundQueue currentQueue = _playList.getSoundQueue(player.getHandle());
        if (currentQueue != null)
            return;

        _playList.addPlayer(player.getHandle());
    }

    @Override
    public boolean canDoPlayerLeave(Player p, LeaveRegionReason reason) {
        return _playList != null;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
        _playList.removePlayer(player.getHandle());
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

            ResourceSound sound = SoundManager.getSound(soundName.trim());
            if (sound == null) {
                Msg.debug("Sound '{0}' not found while loading musical region '{1}'.", soundName, getName());
                continue;
            }

            sounds.add(sound);
        }

        _playList = new PlayList(PVStarAPI.getPlugin(), sounds);
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }
}
