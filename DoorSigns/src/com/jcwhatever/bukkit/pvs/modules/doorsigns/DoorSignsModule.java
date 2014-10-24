package com.jcwhatever.bukkit.pvs.modules.doorsigns;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.signs.EconDoorSignHandler;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.signs.ExpDoorSignHandler;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.signs.ItemDoorSignHandler;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.signs.PointsDoorSignHandler;

public class DoorSignsModule extends PVStarModule {

    private static DoorSignsModule _instance;

    public static DoorSignsModule getInstance() {
        return _instance;
    }

    private DoorManager _doorManager;

    public DoorSignsModule() {
        super();

        _instance = this;
    }

    public DoorManager getDoorManager() {
        return _doorManager;
    }

    @Override
    protected void onRegisterTypes() {

        _doorManager = new DoorManager();
        PVStarAPI.getSignManager().registerSignType(new ItemDoorSignHandler());
        PVStarAPI.getSignManager().registerSignType(new PointsDoorSignHandler());
        PVStarAPI.getSignManager().registerSignType(new ExpDoorSignHandler());

        if (EconomyHelper.hasEconomy()) {
            PVStarAPI.getSignManager().registerSignType(new EconDoorSignHandler());
        }
    }

    @Override
    protected void onEnable() {
        // do nothing
    }

}
