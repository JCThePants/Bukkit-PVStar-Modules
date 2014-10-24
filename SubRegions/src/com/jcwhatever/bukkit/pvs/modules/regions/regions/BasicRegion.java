package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

@RegionTypeInfo(
        name="basic",
        description="Region that does nothing. Used for scripts.")

public class BasicRegion extends AbstractPVRegion {

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        // do nothing
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
        // do nothing
    }

    @Override
    protected void onDisable() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        // do nothing
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return null;
    }
}
