package com.jcwhatever.bukkit.pvs.modules.regions;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.exceptions.MissingTypeInfoException;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.BasicRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.CheckpointRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.CrumbleFloorRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.DamageIntervalRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.DamageRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.DeleteRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.ForwardingRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.MusicRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.PlayerGrinderRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.ReCrumbleFloorRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.SpawnTriggerRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.SpleefFloorRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TeleportRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TellArenaRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TellGameRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TellLobbyRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TellRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.TellSpectatorsRegion;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.maze.MazeRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypesManager {

    private Map<String, Class<? extends AbstractPVRegion>> _regionTypes = new HashMap<>(25);

    TypesManager() {
        register(MazeRegion.class);
        register(BasicRegion.class);
        register(CrumbleFloorRegion.class);
        register(CheckpointRegion.class);
        register(DamageIntervalRegion.class);
        register(DamageRegion.class);
        register(DeleteRegion.class);
        register(ForwardingRegion.class);
        register(MusicRegion.class);
        register(PlayerGrinderRegion.class);
        register(ReCrumbleFloorRegion.class);
        register(SpawnTriggerRegion.class);
        register(SpleefFloorRegion.class);
        register(TeleportRegion.class);
        register(TellRegion.class);
        register(TellArenaRegion.class);
        register(TellGameRegion.class);
        register(TellLobbyRegion.class);
        register(TellSpectatorsRegion.class);
    }

    public void register(Class<? extends AbstractPVRegion> regionClass) {
        PreCon.notNull(regionClass);

        RegionTypeInfo info = regionClass.getAnnotation(RegionTypeInfo.class);
        if (info == null) {
            throw new MissingTypeInfoException(regionClass);
        }

        _regionTypes.put(info.name().toLowerCase(), regionClass);
    }

    public boolean hasType(String regionName) {
        PreCon.notNullOrEmpty(regionName);

        return _regionTypes.containsKey(regionName.toLowerCase());
    }

    public List<String> getRegionTypeNames() {
        return new ArrayList<>(_regionTypes.keySet());
    }

    public List<Class<? extends AbstractPVRegion>> getRegionTypes() {
        return new ArrayList<>(_regionTypes.values());
    }

    public Class<? extends AbstractPVRegion> getRegionType(String typeName) {
        PreCon.notNullOrEmpty(typeName);

        return _regionTypes.get(typeName.toLowerCase());
    }

}
