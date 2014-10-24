package com.jcwhatever.bukkit.pvs.modules.regions.commands;

import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;

public abstract class AbstractRegionCommand extends AbstractPVCommand {

    @Localizable static final String _REGION_NOT_FOUND = "A sub region with the name '{0}' was not found in arena '{1}'.";

    @Nullable
    protected AbstractPVRegion getRegion(CommandSender sender, Arena arena, String regionName) {

        RegionManager manager = SubRegionsModule.getInstance().getManager(arena);
        AbstractPVRegion region = manager.getRegion(regionName);
        if (region == null) {
            tellError(sender, Lang.get(_REGION_NOT_FOUND, regionName, arena.getName()));
            return null; // finish
        }

        return region;
    }

}
