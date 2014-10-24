package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaRespawnEvent;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RegionTypeInfo(
        name="checkpoint",
        description="Changes a players respawn point.")
public class CheckpointRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("spawns", ValueType.STRING, "The name of the spawnpoint to set the players respawn point to.")
        ;
    }

    private List<Spawnpoint> _spawnpoints;
    private Map<UUID, Spawnpoint> _checkpointMap = new HashMap<>(25);

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
        String spawnNames = dataNode.getString("spawns");

        if (spawnNames != null)
            _spawnpoints = getArena().getSpawnManager().getSpawns(spawnNames);
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        Spawnpoint spawn = Rand.get(_spawnpoints);

        _checkpointMap.put(player.getUniqueId(), spawn);
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
    protected boolean canDoPlayerEnter(Player p) {
        return _spawnpoints != null && !_spawnpoints.isEmpty() && !_checkpointMap.containsKey(p.getUniqueId());
    }

    @GenericsEventHandler
    private void onPlayerRespawn(PlayerArenaRespawnEvent event) {

        Spawnpoint spawn = _checkpointMap.get(event.getPlayer().getUniqueId());
        if (spawn == null)
            return;

        event.setRespawnLocation(spawn);
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {
        _checkpointMap.clear();
    }
}
