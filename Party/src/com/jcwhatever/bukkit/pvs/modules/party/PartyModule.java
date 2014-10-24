package com.jcwhatever.bukkit.pvs.modules.party;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.party.commands.PartyCommand;
import com.jcwhatever.bukkit.pvs.modules.party.events.PartyEventListener;

public class PartyModule extends PVStarModule {

    private static PartyModule _instance;

    public static PartyModule getInstance() {
        return _instance;
    }

    private PartyManager _partyManager;

    public PartyModule() {
        _instance = this;
    }

    public PartyManager getManager() {
        return _partyManager;
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    public void onEnable() {

        _partyManager = new PartyManager();

        PVStarAPI.getCommandHandler().registerCommand(PartyCommand.class);
        PVStarAPI.getEventManager().register(new PartyEventListener());
    }

}
