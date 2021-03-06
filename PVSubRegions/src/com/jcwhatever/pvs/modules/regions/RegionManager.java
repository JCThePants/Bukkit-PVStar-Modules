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

import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.modules.regions.regions.AbstractPVRegion;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class RegionManager {

    private final IArena _arena;
    private final IDataNode _dataNode;
    private final Map<String, AbstractPVRegion> _regions = new HashMap<>(30);
    private final SubRegionsModule _module;

    public RegionManager(IArena arena, SubRegionsModule module) {
        _arena = arena;
        _module = module;
        _dataNode = arena.getDataNode("special-regions");

        loadRegions();
    }

    @Nullable
    public AbstractPVRegion getRegion(String regionName) {
        PreCon.notNullOrEmpty(regionName);

        return _regions.get(regionName.toLowerCase());
    }

    public List<AbstractPVRegion> getRegions() {
        return new ArrayList<>(_regions.values());
    }

    @Nullable
    public AbstractPVRegion addRegion(String regionName, String regionType, Location p1, Location p2) {
        PreCon.notNullOrEmpty(regionName);
        PreCon.notNullOrEmpty(regionType);
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        IDataNode dataNode = _dataNode.getNode(regionName);

        AbstractPVRegion region = loadRegion(regionName, regionType, dataNode);
        if (region == null)
            return null;

        dataNode.set("type", region.getTypeName());
        region.setCoords(p1, p2); // also saves data node

        _regions.put(region.getSearchName(), region);

        return region;
    }

    public boolean removeRegion(String regionName) {
        PreCon.notNullOrEmpty(regionName);

        AbstractPVRegion region = _regions.remove(regionName.toLowerCase());
        if (region == null)
            return false;

        region.dispose();

        IDataNode dataNode = _dataNode.getNode(region.getName());
        dataNode.remove();
        dataNode.save();

        return true;
    }

    public void dispose() {

        for (AbstractPVRegion region : _regions.values()) {
            region.dispose();
            IDataNode dataNode = _dataNode.getNode(region.getName());
            dataNode.remove();
        }

        _dataNode.save();
    }

    private void loadRegions() {

        for (IDataNode node : _dataNode) {

            String type = node.getString("type");
            if (type == null)
                continue;

            AbstractPVRegion region = loadRegion(node.getName(), type, node);
            if (region == null)
                continue;

            _regions.put(region.getSearchName(), region);
        }
    }


    @Nullable
    private AbstractPVRegion loadRegion(String regionName, String regionType, IDataNode dataNode) {

        String regionKey = regionName.toLowerCase();

        if (_regions.containsKey(regionKey))
            return null;

        Class<? extends AbstractPVRegion> regionClass = _module.getTypesManager().getRegionType(regionType);
        if (regionClass == null)
            return null;

        RegionTypeInfo typeInfo = regionClass.getAnnotation(RegionTypeInfo.class);
        if (typeInfo == null)
            return null;

        AbstractPVRegion region;
        Constructor<? extends AbstractPVRegion> constructor;

        try {
            constructor = regionClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        try {
            region = constructor.newInstance(regionName);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        region.init(typeInfo, _arena, dataNode, _module);

        return region;
    }
}
