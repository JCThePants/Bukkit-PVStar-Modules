package com.jcwhatever.bukkit.pvs.modules.kitsigns;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.kitsigns.signs.EconKitSignHandler;
import com.jcwhatever.bukkit.pvs.modules.kitsigns.signs.ExpKitSignHandler;
import com.jcwhatever.bukkit.pvs.modules.kitsigns.signs.ItemKitSignHandler;
import com.jcwhatever.bukkit.pvs.modules.kitsigns.signs.PointsKitSignHandler;

public class KitSignsModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getSignManager().registerSignType(new ExpKitSignHandler());
        PVStarAPI.getSignManager().registerSignType(new ItemKitSignHandler());
        PVStarAPI.getSignManager().registerSignType(new PointsKitSignHandler());

        if (EconomyHelper.hasEconomy()) {
            PVStarAPI.getSignManager().registerSignType(new EconKitSignHandler());
        }
    }

    @Override
    protected void onEnable() {
        // do nothing
    }
}
