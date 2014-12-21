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


package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.generic.commands.parameters.ParameterDescription;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
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
                                                                throws InvalidArgumentException {

        specificity = specificity.toLowerCase();

        if (!_settingTypes.contains(specificity)) {
            throw new InvalidArgumentException(new ParameterDescription("specificity",
                            Lang.get(_INVALID_SPECIFICITY, TextUtils.concat(_settingTypes, ", "))));
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

        throw new InvalidArgumentException(new ParameterDescription("specificity",
                        Lang.get(_INVALID_SPECIFICITY, TextUtils.concat(_settingTypes, ", "))));
    }

}
