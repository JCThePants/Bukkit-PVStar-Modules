package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.sounds.PlayList;
import com.jcwhatever.bukkit.generic.sounds.PlayList.PlayerSoundQueue;
import com.jcwhatever.bukkit.generic.sounds.ResourceSound;
import com.jcwhatever.bukkit.generic.sounds.SoundManager;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
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

    @Override
    public boolean canDoPlayerEnter(Player p) {
        return _playList != null && _playList.size() != 0 && super.canDoPlayerEnter(p);
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {

        PlayerSoundQueue currentQueue = _playList.getSoundQueue(player.getHandle());
        if (currentQueue != null)
            return;

        _playList.addPlayer(player.getHandle());
    }

    @Override
    public boolean canDoPlayerLeave(Player p) {
        return _playList != null;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
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
        setIsPlayerWatcher(true);
    }

    @Override
    protected void onDisable() {
        setIsPlayerWatcher(false);
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