package com.jcwhatever.bukkit.pvs.modules.regions.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinition;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.AbstractRegionCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="settings",
        command="info",
        staticParams={ "regionName", "page=1" },
        usage="/{plugin-command} {command} settings info <regionName> [page]",
        description="Displays settings of the specified sub region in the currently selected arena.")

public class InfoSubCommand extends AbstractRegionCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Sub Region '{0}'.";
    @Localizable static final String _LABEL_ENABLED = "ENABLED";
    @Localizable static final String _LABEL_TYPE = "TYPE";
    @Localizable static final String _LABEL_DEFINED = "DEFINED";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        String regionName = args.getName("regionName");
        int page = args.getInt("page");

        AbstractPVRegion region = getRegion(sender, arena, regionName);
        if (region == null)
            return; // finish

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, region.getName()));

        pagin.add(Lang.get(_LABEL_ENABLED), region.isEnabled());
        pagin.add(Lang.get(_LABEL_TYPE), region.getTypeName());
        pagin.add(Lang.get(_LABEL_DEFINED), region.isDefined());

        if (region.isDefined()) {
            Location p1 = region.getP1();
            Location p2 = region.getP2();

            pagin.add("P1", TextUtils.formatLocation(p1, true));
            pagin.add("P2", TextUtils.formatLocation(p2, true));
        }

        SettingDefinitions defs = region.getSettingsManager().getPossibleSettings();

        if (defs.size() > 0) {

            for (SettingDefinition def : defs.values()) {
                Object value = region.getSettingsManager().get(def.getConfigName(), true);

                pagin.add(def.getConfigName(), value);
            }
        }

        pagin.show(sender, page, FormatTemplate.DEFINITION);
    }

}