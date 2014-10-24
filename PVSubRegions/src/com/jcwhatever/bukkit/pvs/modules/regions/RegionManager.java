package com.jcwhatever.bukkit.pvs.modules.regions;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegionManager {

    private final Arena _arena;
    private final IDataNode _dataNode;
    private final Map<String, AbstractPVRegion> _regions = new HashMap<>(30);
    private final SubRegionsModule _module;

    public RegionManager(Arena arena, SubRegionsModule module) {
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
        dataNode.saveAsync(null);

        return true;
    }

    public void dispose() {

        for (AbstractPVRegion region : _regions.values()) {
            region.dispose();
            IDataNode dataNode = _dataNode.getNode(region.getName());
            dataNode.remove();
        }

        _dataNode.saveAsync(null);
    }

    private void loadRegions() {

        Set<String> regionNames = _dataNode.getSubNodeNames();

        for (String regionName : regionNames) {

            IDataNode node = _dataNode.getNode(regionName);

            String type = node.getString("type");
            if (type == null)
                continue;

            AbstractPVRegion region = loadRegion(regionName, type, node);
            if (region == null)
                continue;

            _regions.put(region.getSearchName(), region);
        }
    }


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

        try {
            region = regionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        region.init(regionName, typeInfo, _arena, dataNode, _module);

        return region;
    }
}