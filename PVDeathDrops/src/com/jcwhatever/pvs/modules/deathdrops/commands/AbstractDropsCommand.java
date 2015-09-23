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


package com.jcwhatever.pvs.modules.deathdrops.commands;

import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.parameters.IParameterDescription;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.pvs.modules.deathdrops.Lang;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class AbstractDropsCommand extends AbstractPVCommand {

    @Localizable static final String _INVALID_SPECIFICITY =
            "Specificity can be one of the following values: {0: values list}";

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
            throw CommandException.invalidArgument(getRegistered(), new SpecificityParameter());
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

        throw CommandException.invalidArgument(getRegistered(), new SpecificityParameter());
    }

    private class SpecificityParameter implements IParameterDescription {

        @Override
        public Plugin getPlugin() {
            return AbstractDropsCommand.this.getPlugin();
        }

        @Override
        public String getName() {
            return "specificity";
        }

        @Override
        public String getDescription() {
            return Lang.get(_INVALID_SPECIFICITY, TextUtils.concat(_settingTypes, ", ")).toString();
        }
    }

}
