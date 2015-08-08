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


package com.jcwhatever.pvs.modules.randombox.commands.items;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.randombox.ChestItems;
import com.jcwhatever.pvs.modules.randombox.Lang;
import com.jcwhatever.pvs.modules.randombox.RandomBoxExtension;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

@CommandInfo(
        command="adddefault",
        description="Add default items to random boxes in the selected arena.")

public class AddDefaultSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Default random box items added to arena '{0: arena name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNING);
        if (arena == null)
            return; // finish

        RandomBoxExtension extension = getExtension(sender, arena, RandomBoxExtension.class);
        if (extension == null) {
            return; // finish
        }

        ChestItems items = extension.getItems();

        items.add(item(Material.WOOD_SWORD), 50);
        items.add(item(Material.WOOD_SWORD, Enchantment.KNOCKBACK, 1), 15);
        items.add(item(Material.WOOD_SWORD, Enchantment.DAMAGE_ALL, 1), 10);
        items.add(item(Material.WOOD_SWORD, Enchantment.DAMAGE_ALL, 2), 5);
        items.add(item(Material.WOOD_SWORD, Enchantment.DAMAGE_ALL, 3), 3);

        items.add(item(Material.STONE_SWORD), 40);
        items.add(item(Material.STONE_SWORD, Enchantment.KNOCKBACK, 1), 10);
        items.add(item(Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 1), 5);
        items.add(item(Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 2), 3);
        items.add(item(Material.STONE_SWORD, Enchantment.DAMAGE_ALL, 3), 1);

        items.add(item(Material.IRON_SWORD), 20);
        items.add(item(Material.IRON_SWORD, Enchantment.KNOCKBACK, 1), 10);
        items.add(item(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 1), 5);
        items.add(item(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 2), 3);
        items.add(item(Material.IRON_SWORD, Enchantment.DAMAGE_ALL, 3), 1);

        items.add(item(Material.ARROW, 20), 40);

        items.add(item(Material.DIAMOND_SWORD), 10);
        items.add(item(Material.DIAMOND_SWORD, Enchantment.KNOCKBACK, 1), 7);
        items.add(item(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 1), 5);
        items.add(item(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 2), 3);
        items.add(item(Material.DIAMOND_SWORD, Enchantment.DAMAGE_ALL, 3), 1);

        items.add(item(Material.BOW), 5);
        items.add(item(Material.BOW, Enchantment.ARROW_DAMAGE, 1), 4);
        items.add(item(Material.BOW, Enchantment.ARROW_INFINITE, 1), 3);

        items.add(new ItemStack(Material.GOLD_SWORD), 1);

        tellSuccess(sender, Lang.get(_SUCCESS, arena.getName()));
    }

    private ItemStack item(Material material) {
        return item(material, 1, null, 1);
    }

    private ItemStack item(Material material, int qty) {
        return item(material, qty, null, 1);
    }

    private ItemStack item(Material material, @Nullable Enchantment enchantment, int level) {
        return item(material, 1, enchantment, level);
    }

    private ItemStack item(Material material, int qty, @Nullable Enchantment enchantment, int level) {

        ItemStack item = new ItemStack(material, qty);

        if (enchantment != null) {
            item.addEnchantment(enchantment, level);
        }

        return item;
    }
}