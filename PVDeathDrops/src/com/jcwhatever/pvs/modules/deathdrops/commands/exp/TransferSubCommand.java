/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.pvs.modules.deathdrops.commands.exp;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.pvs.modules.deathdrops.Lang;
import com.jcwhatever.pvs.modules.deathdrops.commands.AbstractDropsCommand;
import com.jcwhatever.pvs.modules.deathdrops.commands.TransferType;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="exp",
        command="transfer",
        staticParams = { "specificity", "drop|direct|clear|info=info"},
        description="View or set how dropped exp is transferred to a player in the " +
                "selected arena at the specified specificity.",

        paramDescriptions = {
                "specificity= Specify what scope the setting applies to. " +
                        "Use 'global' for all, 'player' for players, 'mobs' for all mobs, " +
                        "or specify the mob EntityType name. More specific settings " +
                        "override general settings.",
                "drop|direct|clear|info= Use 'drop' to drop the exp on the ground, 'direct' to add " +
                        "the exp directly to the player, 'clear' to remove the setting, " +
                        "'info' or leave blank to see current setting."})

public class TransferSubCommand extends AbstractDropsCommand implements IExecutableCommand {

    @Localizable static final String _INFO_DIRECT =
            "Exp is transferred directly to the player in arena '{0: arena name}'.";

    @Localizable static final String _INFO_DROP =
            "Exp is dropped in arena '{0: arena name}'.";

    @Localizable static final String _SET_DIRECT =
            "Exp transfer type changed to DIRECT in arena '{0: arena name}'.";

    @Localizable static final String _SET_DROP =
            "Exp transfer type changed to DROP in arena '{0: arena name}'.";

    @Localizable static final String _CLEAR =
            "Value cleared for specificity '{0: specificity name}' in arena '{1: arena name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "drop|direct|clear|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("drop|direct|clear|info").equals("info")) {

            boolean isDirect = settings.isDirectExpTransfer();

            if (isDirect)
                tell(sender, Lang.get(_INFO_DIRECT, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_DROP, arena.getName()));
        }
        else if (args.getString("drop|direct|clear|info").equalsIgnoreCase("clear")) {

            settings.clearDirectExpTransfer();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {

            TransferType type = args.getEnum("drop|direct|clear|info", TransferType.class);

            settings.setDirectExpTransfer(type == TransferType.DIRECT);

            if (type == TransferType.DIRECT)
                tellSuccess(sender, Lang.get(_SET_DIRECT, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_DROP, arena.getName()));
        }
    }
}
