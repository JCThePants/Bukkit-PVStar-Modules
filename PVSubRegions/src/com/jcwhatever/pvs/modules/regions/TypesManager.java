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


package com.jcwhatever.pvs.modules.regions;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.pvs.modules.regions.regions.BasicRegion;
import com.jcwhatever.pvs.modules.regions.regions.CheckpointRegion;
import com.jcwhatever.pvs.modules.regions.regions.CrumbleFloorRegion;
import com.jcwhatever.pvs.modules.regions.regions.DamageIntervalRegion;
import com.jcwhatever.pvs.modules.regions.regions.DamageRegion;
import com.jcwhatever.pvs.modules.regions.regions.DeleteRegion;
import com.jcwhatever.pvs.modules.regions.regions.ForwardingRegion;
import com.jcwhatever.pvs.modules.regions.regions.MusicRegion;
import com.jcwhatever.pvs.modules.regions.regions.PlayerGrinderRegion;
import com.jcwhatever.pvs.modules.regions.regions.ReCrumbleFloorRegion;
import com.jcwhatever.pvs.modules.regions.regions.SpawnTriggerRegion;
import com.jcwhatever.pvs.modules.regions.regions.SpleefFloorRegion;
import com.jcwhatever.pvs.modules.regions.regions.TeleportRegion;
import com.jcwhatever.pvs.modules.regions.regions.TellArenaRegion;
import com.jcwhatever.pvs.modules.regions.regions.TellGameRegion;
import com.jcwhatever.pvs.modules.regions.regions.TellLobbyRegion;
import com.jcwhatever.pvs.modules.regions.regions.TellRegion;
import com.jcwhatever.pvs.modules.regions.regions.TellSpectatorsRegion;
import com.jcwhatever.pvs.modules.regions.regions.maze.MazeRegion;

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
            throw new IllegalArgumentException(
                    "Expected but did not find proper type info annotation on " +
                            "class: " + regionClass.getName());
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
