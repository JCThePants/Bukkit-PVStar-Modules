package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DropSettings;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class AbstractDropsCommand extends AbstractPVCommand {

    @Localizable static final String _INVALID_SPECIFICITY = "Specificity can be one of the following values: {0}";

    private static List<String> _settingTypes = new ArrayList<>(EntityType.values().length + 3);

    static {

        _settingTypes.add("global");
        _settingTypes.add("player");
        _settingTypes.add("mobs");

        for (EntityType type : EntityType.values()) {
            if (!type.isAlive())
                continue;

            _settingTypes.add(type.name().toLowerCase());
        }
    }

    protected List<String> getSpecificityTypes() {
        return new ArrayList<>(_settingTypes);
    }

    protected DropSettings getDropSettings(String specificity, DeathDropsExtension extension)
                                                                throws InvalidValueException {

        specificity = specificity.toLowerCase();

        if (!_settingTypes.contains(specificity)) {
            throw new InvalidValueException("specificity", Lang.get(_INVALID_SPECIFICITY, TextUtils.concat(_settingTypes, ", ")));
        }

        switch (specificity) {
            case "global":

                return extension.getGlobalSettings();
            case "player":

                return extension.getPlayerSettings();
            case "mobs":

                return extension.getMobSettings();
            default:

                for (EntityType type : EntityType.values()) {
                    if (!type.isAlive())
                        continue;

                    if (specificity.equalsIgnoreCase(type.name())) {

                        return extension.getLivingEntitySettings(type);
                    }
                }

                break;
        }

        throw new InvalidValueException("specificity", Lang.get(_INVALID_SPECIFICITY, TextUtils.concat(_settingTypes, ", ")));
    }

}
