package com.jcwhatever.bukkit.pvs.modules.notesigns.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="notes",
        command="hideall",
        usage="/{plugin-command} {command} hideall",
        description="Hide all notes in all arenas.")

public class HideAllSubCommand extends AbstractPVCommand {

    @Localizable
    static final String _SUCCESS = "{0} Note signs hidden.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        int hideCount = 0;
        List<SignContainer> signs = PVStarAPI.getSignManager().getSigns("Note");
        if (signs != null) {

            for (SignContainer sign : signs) {

                IDataNode signNode = sign.getDataNode();
                if (signNode == null)
                    continue;

                Sign s = sign.getSign();
                if (s == null)
                    continue;

                s.getBlock().setType(Material.AIR);
                hideCount++;
            }
        }

        tellSuccess(sender, Lang.get(_SUCCESS, hideCount));
    }
}