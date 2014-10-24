package com.jcwhatever.bukkit.pvs.modules.notesigns.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        command={ "notes", "pvnotes", "adminnotes" },
        description="Manage arena chests.")

public class NotesCommand extends AbstractPVCommand {

    public NotesCommand() {
        super();

        registerSubCommand(ClearSubCommand.class);
        registerSubCommand(HideSubCommand.class);
        registerSubCommand(HideAllSubCommand.class);
        registerSubCommand(ShowSubCommand.class);
    }
}
