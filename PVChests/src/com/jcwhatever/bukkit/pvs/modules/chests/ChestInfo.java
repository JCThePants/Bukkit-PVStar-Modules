package com.jcwhatever.bukkit.pvs.modules.chests;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

public class ChestInfo {

    private final Location _location;
    private final ItemStack[] _contents;
    private final int _hash;

    public ChestInfo (Location location, ItemStack[] contents) {
        _location = location;
        _contents = contents;
        _hash = location.hashCode();
    }

    public Location getLocation() {
        return _location;
    }

    public ItemStack[] getPresetContents() {
        return _contents;
    }

    public Chest getChest() {
        BlockState state = _location.getBlock().getState();

        if (state instanceof Chest)
            return (Chest)state;

        return null;
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChestInfo) {
            return ((ChestInfo)obj).getLocation().equals(_location);
        }
        else if (obj instanceof Location) {
            return obj.equals(_location);
        }
        return false;
    }
}
