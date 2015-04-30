package com.jcwhatever.pvs.modules.pv2d;

import com.jcwhatever.nucleus.regions.SimpleRegionSelection;

import org.bukkit.Location;

/**
 * A tile in a 2d arena region.
 */
public class Tile extends SimpleRegionSelection {

    private final int _tileX;
    private final int _tileZ;

    public Tile(int tileX, int tileZ, Location p1, Location p2) {
        super(p1, p2);

        _tileX = tileX;
        _tileZ = tileZ;
    }

    public int getTileX() {
        return _tileX;
    }

    public int getTileZ() {
        return _tileZ;
    }
}
