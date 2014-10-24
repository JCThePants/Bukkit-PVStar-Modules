package com.jcwhatever.bukkit.pvs.modules.notesigns;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.notesigns.commands.NotesCommand;
import com.jcwhatever.bukkit.pvs.modules.notesigns.signs.NoteSignHandler;

public class NoteSignsModule extends PVStarModule {

    private static NoteSignsModule _instance;
    private NoteSignHandler _handler;

    public static NoteSignsModule getInstances() {
        return _instance;
    }

    public NoteSignsModule() {
        _instance = this;
    }

    public NoteSignHandler getNoteHandler() {
        return _handler;
    }

    @Override
    protected void onRegisterTypes() {

        _handler = new NoteSignHandler();

        if (PVStarAPI.getSignManager().registerSignType(_handler)) {

            PVStarAPI.getCommandHandler().registerCommand(NotesCommand.class);
        }
    }

    @Override
    protected void onEnable() {

    }
}
