package com.jcwhatever.pvs.modules.pv2d;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords2Di;
import com.jcwhatever.pvs.api.arena.ArenaRegion;
import com.jcwhatever.pvs.api.arena.IArena;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 
 */
public class ScriptApi implements IDisposable {

    private static final MutableCoords2Di COORDS = new MutableCoords2Di();
    private boolean _isDisposed;
    private Map<Coords2Di, MetaStore> _meta = new HashMap<>(10);

    /**
     * Get script api for specified arena.
     *
     * @param arena  The arena.
     */
    public ArenaScriptApi get(IArena arena) {
        PreCon.notNull(arena);
        return new ArenaScriptApi(arena);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    public class ArenaScriptApi {

        private IArena _arena;

        ArenaScriptApi(IArena arena) {
            _arena = arena;
            ext().registerScript(this);
        }

        public int getXWidth() {
            return ext().getXWidth();
        }

        public int getZWidth() {
            return ext().getZWidth();
        }

        public int getBlockSize() {
            return ext().getBlockSize();
        }

        public List<Tile> getTiles() {
            return ext().getTiles();
        }

        public Tile getTile(int tileX, int tileZ) {
            return ext().getTile(tileX, tileZ);
        }

        @Nullable
        public Tile getTile(Location location) {

            PV2DExtension ext = ext();
            ArenaRegion region = ext.getArena().getRegion();

            World world = region.getWorld();
            if (world == null)
                return null;

            if (!world.equals(location.getWorld()))
                return null;

            double x = region.getXBlockWidth() - (region.getXEnd() - location.getBlockX());
            double z = region.getZBlockWidth() - (region.getZEnd() - location.getBlockZ());

            if (x < 0 || z < 0)
                return null;

            if (x > region.getXBlockWidth() || z > region.getZBlockWidth())
                return null;

            return getTile(
                    (int)Math.floor(x / ext.getBlockSize()),
                    (int)Math.floor(z / ext.getBlockSize()));
        }

        public MetaStore getMeta(Tile tile) {
            PreCon.notNull(tile);

            return getMeta(tile.getTileX(), tile.getTileZ());
        }

        public MetaStore getMeta(int x, int z) {
            COORDS.setX(x);
            COORDS.setZ(z);

            MetaStore meta = _meta.get(COORDS);
            if (meta == null) {
                meta = new MetaStore();
                _meta.put(new Coords2Di(COORDS), meta);
            }

            return meta;
        }

        void onRegionDefined() {
            _meta.clear();
        }

        private PV2DExtension ext() {
            PV2DExtension extension = _arena.getExtensions().get(PV2DExtension.class);
            if (extension == null)
                throw new IllegalStateException("PV2D extension not installed.");

            return extension;
        }
    }
}
