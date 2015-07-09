package com.jcwhatever.pvs.modules.pv2d;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords2Di;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.ArenaRegion;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.region.ArenaRegionDefinedEvent;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.pv2d.ScriptApi.ArenaScriptApi;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 2D arena region tiles extension.
 */
@ArenaExtensionInfo(
        name = "PV2DGrid",
        description = "2D region grid tiling extension used by scripts."
)
public class PV2DExtension extends ArenaExtension implements IEventListener {

    private static final MutableCoords2Di COORDS = new MutableCoords2Di();

    private int _blockSize = 3;
    private int _xWidth;
    private int _zWidth;
    private Map<Coords2Di, Tile> _tiles;
    private Map<ArenaScriptApi, Void> _script = new WeakHashMap<>(7);

    public int getXWidth() {
        return _xWidth;
    }

    public int getZWidth() {
        return _zWidth;
    }

    public int getBlockSize() {
        return _blockSize;
    }

    public void setBlockSize(int size) {
        _blockSize = size;

        getDataNode().set("block-size", size);
        getDataNode().save();

        loadTiles();
    }

    public List<Tile> getTiles() {
        if (_tiles == null) {
            throw new IllegalStateException("PV2DExtension for Arena '" + getArena().getName()
                    + "' is not enabled.");
        }

        return new ArrayList<>(_tiles.values());
    }

    public Tile getTile(int x, int z) {

        COORDS.setX(x);
        COORDS.setZ(z);

        return _tiles.get(COORDS);
    }

    void registerScript(ArenaScriptApi api) {
        _script.put(api, null);
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onEnable() {

        _blockSize = getDataNode().getInteger("block-size", _blockSize);

        loadTiles();
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    private boolean loadTiles() {

        if (_tiles != null) {
            _tiles.clear();
        }

        ArenaRegion region = getArena().getRegion();

        if (!region.isDefined()) {
            Msg.warning("Region for arena '{0}' not defined yet.", getArena().getName());
            return false;
        }

        boolean hasError = false;
        if (region.getXBlockWidth() % _blockSize != 0) {
            Msg.warning("Region for arena '{0}' does not have an X axis width " +
                    "that is a multiple of {1}.", getArena().getName(), _blockSize);
            hasError = true;
        }

        if (region.getZBlockWidth() % _blockSize != 0) {
            Msg.warning("Region for arena '{0}' does not have a Z axis width " +
                    "that is a multiple of {1}.", getArena().getName(), _blockSize);
            hasError = true;
        }

        if (hasError)
            return false;

        _xWidth = region.getXBlockWidth() / _blockSize;
        _zWidth = region.getZBlockWidth() / _blockSize;

        _tiles = new HashMap<>((region.getXBlockWidth() * region.getZBlockWidth() / _blockSize));

        for (int x = 0; x < _xWidth; x++) {
            for (int z=0; z < _zWidth; z++) {

                int locX = region.getXStart() + (x * _blockSize);
                int locZ = region.getZStart() + (z * _blockSize);

                Location p1 = new Location(region.getWorld(), locX, region.getYStart(), locZ);
                Location p2 = new Location(region.getWorld(),
                        locX + _blockSize - 1, region.getYEnd(), locZ + _blockSize -1);

                Tile tile = new Tile(x, z, p1, p2);
                Coords2Di coords = new Coords2Di(x, z);
                _tiles.put(coords, tile);
            }
        }

        return true;
    }

    @EventMethod
    private void onArenaRegionDefined(@SuppressWarnings("unused") ArenaRegionDefinedEvent event) {

        for (ArenaScriptApi api : _script.keySet()) {
            api.onRegionDefined();
        }

        loadTiles();
    }
}
