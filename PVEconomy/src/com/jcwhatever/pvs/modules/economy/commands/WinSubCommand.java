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


package com.jcwhatever.pvs.modules.economy.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.Economy;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.economy.EconomyExtension;
import com.jcwhatever.pvs.modules.economy.Lang;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="economy",
        command="win",
        staticParams={"amount=info"},
        usage="/{plugin-command} economy win [amount]",
        description="Set or view the currency amount rewarded/deducted from players " +
                "when they win in the selected arena.",

        paramDescriptions = {
                "amount= The currency amount. Negative numbers to deduct. " +
                        "Leave blank to see current setting."})

public class WinSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVEconomy extension not installed in arena '{0}'.";
    @Localizable static final String _INFO_NO_REWARD = "Players do not receive or lose anything when they win in arena '{0}'.";
    @Localizable static final String _INFO_REWARD = "Players are rewarded {0} when they win in arena '{1}'.";
    @Localizable static final String _INFO_PENALTY = "{0} is deducted from a player that wins in arena '{1}'.";
    @Localizable static final String _SET_NO_REWARD = "Removed player economy rewards for winning in arena '{0}'.";
    @Localizable static final String _SET_REWARD = "Set player economy reward for winning in arena '{0}' to {1}.";
    @Localizable static final String _SET_PENALTY = "Set player economy penalty for winning in arena '{0}' to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "amount"));
        if (arena == null)
            return; // finish

        EconomyExtension extension = arena.getExtensionManager().get(EconomyExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; //finish
        }

        if (args.getString("amount").equals("info")) {

            double amount = extension.getWinAmount();

            if (Double.compare(amount, 0.0D) == 0) {
                tell(sender, Lang.get(_INFO_NO_REWARD, arena.getName()));
            }
            else if (amount > 0.0D) {
                tell(sender, Lang.get(_INFO_REWARD, Economy.getCurrency().format(amount), arena.getName()));
            }
            else {
                tell(sender, Lang.get(_INFO_PENALTY, Economy.getCurrency().format(amount), arena.getName()));
            }
        }
        else {

            double amount = args.getDouble("amount");

            extension.setWinAmount(amount);

            if (Double.compare(amount, 0.0D) == 0) {
                tellSuccess(sender, Lang.get(_SET_NO_REWARD, arena.getName()));
            }
            else if (amount > 0.0D) {
                tellSuccess(sender, Lang.get(_SET_REWARD, arena.getName(),
                        Economy.getCurrency().format(amount)));
            }
            else {
                tellSuccess(sender, Lang.get(_SET_PENALTY, arena.getName(),
                        Economy.getCurrency().format(Math.abs(amount))));
            }
        }
    }
}
