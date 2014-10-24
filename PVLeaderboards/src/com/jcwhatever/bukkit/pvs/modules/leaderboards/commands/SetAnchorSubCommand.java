package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException.CommandSenderType;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@ICommandInfo(
        parent="lb",
        command="setanchor",
        staticParams={"leaderboardName"},
        usage="/{plugin-command} {command} setanchor <leaderboardName>",
        description="Sets the leaderboards anchor sign (top left) to the sign the player clicks on.")

public class SetAnchorSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _NOT_A_SIGN = "The block you selected is not a sign.";
    @Localizable static final String _SELECT_SIGN = "Please click on the leaderboard anchor sign...";
    @Localizable static final String _ANCHOR_SET = "Leaderboard '{0}' anchor set.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        InvalidCommandSenderException.check(sender, CommandSenderType.PLAYER, "Console cannot set location.");

        String leaderboardName = args.getString("leaderboardName");

        final Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        Player p = (Player)sender;

        PlayerBlockSelect.query(p, new PlayerBlockSelectHandler() {
            @Override
            public boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction) {


                if (!(selectedBlock.getState() instanceof Sign)) {

                    tellError(p, Lang.get(_NOT_A_SIGN));
                } else {

                    Sign sign = (Sign) selectedBlock.getState();

                    leaderboard.setAnchor(sign);

                    tellSuccess(p, Lang.get(_ANCHOR_SET, leaderboard.getName()));
                }
                return true;
            }
        });


        tell(sender, Lang.get(_SELECT_SIGN));
    }
}
