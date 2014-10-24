package com.jcwhatever.bukkit.pvs.modules.citizens.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits.KitsCommand;

@ICommandInfo(
        command={ "npc", "citizensnpc" },
        description="Manage NPC kits.")

public class NpcCommand extends AbstractPVCommand {

    public NpcCommand() {
        super();

        registerSubCommand(KitsCommand.class);
    }
}