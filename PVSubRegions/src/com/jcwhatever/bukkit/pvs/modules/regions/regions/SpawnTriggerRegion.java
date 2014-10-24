package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RegionTypeInfo(
        name="spawntrigger",
        description="Trigger mob spawners by entering the region.")

public class SpawnTriggerRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
            .set("spawn-count", 1, ValueType.INTEGER, "Set the number of entities each spawn will created when the region is triggered.")
            .set("spawns", ValueType.STRING, "Set the spawns that are triggered using a comma delimited list of spawn names.")
            .set("max-triggers", 1, ValueType.INTEGER, "Set the maximum times the region can be triggered.")
        ;
    }

    private List<Spawnpoint> _spawns;
    private int _spawnCount = 1;
    private int _maxTriggers = 1;

    private int _triggerCount = 0;

    @Override
    protected boolean canDoPlayerEnter(Player p) {
        return isEnabled() && _triggerCount < _maxTriggers;
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        for (Spawnpoint spawn : _spawns) {
            spawn.spawn(getArena(), _spawnCount);
        }
        _triggerCount++;
    }

    @Override
    protected boolean canDoPlayerLeave(Player p) {
        return false;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
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
        setIsPlayerWatcher(true);
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        setIsPlayerWatcher(false);
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {

        _spawnCount = dataNode.getInteger("spawn-count", _spawnCount);
        _maxTriggers = dataNode.getInteger("max-triggers", _maxTriggers);

        String rawSpawnNames = dataNode.getString("spawns", "");

        String[] nameComp = TextUtils.PATTERN_COMMA.split(rawSpawnNames);
        _spawns = new ArrayList<>(nameComp.length);

        for (String untrimmed : nameComp) {

            String spawnName = untrimmed.trim();
            if (spawnName.isEmpty())
                continue;

            Spawnpoint spawn = getArena().getSpawnManager().getSpawn(spawnName);
            if (spawn != null && spawn.getSpawnType().isSpawner())
                _spawns.add(spawn);
        }
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {
        _triggerCount = 0;
    }
}
