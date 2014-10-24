package com.jcwhatever.bukkit.pvs.modules.notesigns.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NoteSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Note";
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.BOLD.toString() + TextColor.DARK_BLUE;
    }

    @Override
    public String getDescription() {
        return "Note signs that can be hidden.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Note",
                "--anything--",
                "--anything--",
                "--anything--"
        };
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {

        Arena arena = PVStarAPI.getArenaManager().getArena(sign.getLocation());
        if (arena == null) {
            Msg.tellError(p, "No arena found at this location.");
            return false;
        }

        if (sign.getDataNode() != null)
            sign.getDataNode().set("arena-id", arena.getId());

        return true;
    }

    @Override
    protected boolean onSignClick(Player p, SignContainer sign) {
        return false;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {
        return true;
    }
}
