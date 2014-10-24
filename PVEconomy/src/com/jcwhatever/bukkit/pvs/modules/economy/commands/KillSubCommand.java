package com.jcwhatever.bukkit.pvs.modules.economy.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.economy.EconomyExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="economy",
        command="kill",
        staticParams={"amount=info"},
        usage="/{plugin-command} economy kill [amount]",
        description="Set or view the currency amount rewarded/deducted from players when they kill in the selected arena.")

public class KillSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVEconomy extension not installed in arena '{0}'.";
    @Localizable static final String _INFO_NO_REWARD = "Players do not receive anything when they kill in arena '{0}'.";
    @Localizable static final String _INFO_REWARD = "Players are rewarded {0} when they kill in arena '{1}'.";
    @Localizable static final String _INFO_PENALTY = "{0} is deducted from a player that kills in arena '{1}'.";
    @Localizable static final String _SET_NO_REWARD = "Removed player economy rewards for killing in arena '{0}'.";
    @Localizable static final String _SET_REWARD = "Set player economy reward for killing in arena '{0}' to {1}.";
    @Localizable static final String _SET_PENALTY = "Set player economy penalty for killing in arena '{0}' to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "amount"));
        if (arena == null)
            return; // finish

        EconomyExtension extension = arena.getExtensionManager().get(EconomyExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; //finish
        }

        if (args.getString("amount").equals("info")) {

            double amount = extension.getKillAmount();

            if (Double.compare(amount, 0.0D) == 0) {
                tell(sender, Lang.get(_INFO_NO_REWARD, arena.getName()));
            }
            else if (amount > 0.0D) {
                tell(sender, Lang.get(_INFO_REWARD, EconomyHelper.formatAmount(amount), arena.getName()));
            }
            else {
                tell(sender, Lang.get(_INFO_PENALTY, EconomyHelper.formatAmount(amount), arena.getName()));
            }
        }
        else {

            double amount = args.getDouble("amount");

            extension.setKillAmount(amount);

            if (Double.compare(amount, 0.0D) == 0) {
                tellSuccess(sender, Lang.get(_SET_NO_REWARD, arena.getName()));
            }
            else if (amount > 0.0D) {
                tellSuccess(sender, Lang.get(_SET_REWARD, arena.getName(), EconomyHelper.formatAmount(amount)));
            }
            else {
                tellSuccess(sender, Lang.get(_SET_PENALTY, arena.getName(), EconomyHelper.formatAmount(Math.abs(amount))));
            }
        }
    }
}

