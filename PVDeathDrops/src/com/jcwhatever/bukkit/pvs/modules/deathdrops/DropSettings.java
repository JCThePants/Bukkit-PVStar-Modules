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


package com.jcwhatever.bukkit.pvs.modules.deathdrops;

import com.jcwhatever.generic.storage.DataType;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class DropSettings {

    private final DropSettings _parent;
    private final IDataNode _dataNode;

    private Set<String> _setSettings = new HashSet<String>(9);

    // Item settings
    private ItemStack[] _itemRewards = new ItemStack[] { new ItemStack(Material.GOLD_NUGGET) };

    private boolean _isItemDropEnabled = false;
    private boolean _isDirectItemTransfer = false;
    private boolean _isRandomItemDrop = false;
    private double _itemDropRate = 100.0D;

    // EXP settings
    private boolean _isExpEnabled = false;
    private boolean _isDirectExpTransfer = false;
    private double _expDropRate = 100.0D;
    private int _expDropAmount = 1;

    public DropSettings(@Nullable DropSettings parent, IDataNode dataNode) {
        _parent = parent;
        _dataNode = dataNode;

        loadSettings();
    }

    public ItemStack[] getItemRewards() {
        return getTopMost("item-rewards")._itemRewards;
    }

    public void setItemRewards(ItemStack[] items) {
        PreCon.notNull(items);

        _itemRewards = items.clone();
        save("items-rewards", _itemRewards);
    }

    public boolean isItemDropEnabled() {
        return getTopMost("item-drops-enabled")._isItemDropEnabled;
    }

    public void setItemDropEnabled(boolean isEnabled) {

        _isItemDropEnabled = isEnabled;
        save("item-drops-enabled", isEnabled);
    }

    public void clearItemDropEnabled() {
        clear("item-drops-enabled");
    }

    public boolean isDirectItemTransfer() {
        return getTopMost("direct-item-transfer")._isDirectItemTransfer;
    }

    public void setDirectItemTransfer(boolean isEnabled) {

        _isDirectItemTransfer = isEnabled;
        save("direct-item-transfer", isEnabled);
    }

    public void clearDirectItemTransfer() {
        clear("direct-item-transfer");
    }

    public boolean isRandomItemDrop() {
        return getTopMost("random-items")._isRandomItemDrop;
    }

    public void setRandomItemDrop(boolean isEnabled) {

        _isRandomItemDrop = isEnabled;
        save("random-items", isEnabled);
    }

    public void clearRandomItemDrop() {
        clear("random-items");
    }

    public double getItemDropRate() {
        return getTopMost("item-drop-rate")._itemDropRate;
    }

    public void setItemDropRate(double rate) {

        _itemDropRate = rate;
        save("item-drop-rate", rate);
    }

    public void clearItemDropRate() {
        clear("item-drop-rate");
    }

    public boolean isExpDropEnabled() {
        return getTopMost("exp-drops-enabled")._isExpEnabled;
    }

    public void setExpDropEnabled(boolean isEnabled) {

        _isExpEnabled = isEnabled;
        save("exp-drops-enabled", isEnabled);
    }

    public void clearExpDropEnabled() {
        clear("exp-drops-enabled");
    }

    public boolean isDirectExpTransfer() {
        return getTopMost("direct-exp-transfer")._isDirectExpTransfer;
    }

    public void setDirectExpTransfer(boolean isEnabled) {

        _isDirectExpTransfer = isEnabled;
        save("direct-exp-transfer", isEnabled);
    }

    public void clearDirectExpTransfer() {
        clear("direct-exp-transfer");
    }

    public double getExpDropRate() {
        return getTopMost("exp-drop-rate")._expDropRate;
    }

    public void setExpDropRate(double rate) {

        _expDropRate = rate;
        save("exp-drop-rate", rate);
    }

    public void clearExpDropRate() {
        clear("exp-drop-rate");
    }

    public int getExpDropAmount() {
        return getTopMost("exp-drop-amount")._expDropAmount;
    }

    public void setExpDropAmount(int amount) {

        _expDropAmount = amount;
        save("exp-drop-amount", amount);
    }

    public void clearExpDropAmount() {
        clear("exp-drop-amount");
    }


    private DropSettings getTopMost(String settingName) {

        DropSettings settings = this;

        while (true) {

            if (settings == null) // to satisfy code checks
                break;

            if (settings._setSettings.contains(settingName))
                break;

            if (settings._parent == null)
                break;

            settings = settings._parent;
        }

        return settings;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(String valueName, DataType type, T defaultValue) {
        Object value = _dataNode.get(valueName, type);
        if (value == null)
            return defaultValue;

        _setSettings.add(valueName);
        return (T)value;
    }

    private void save(String dataName, Object value) {
        _setSettings.add(dataName);

        _dataNode.set(dataName, value);
        _dataNode.saveAsync(null);
    }

    private void clear(String dataName) {
        _setSettings.remove(dataName);
        _dataNode.set(dataName, null);
        _dataNode.saveAsync(null);
    }

    private void loadSettings() {

        _itemRewards = getValue("item-rewards", DataType.ITEMSTACKS, _itemRewards);
        _isItemDropEnabled = getValue("item-drops-enabled", DataType.BOOLEAN, _isItemDropEnabled);
        _isDirectItemTransfer = getValue("direct-item-transfer", DataType.BOOLEAN, _isDirectItemTransfer);
        _isRandomItemDrop = getValue("random-items", DataType.BOOLEAN, _isRandomItemDrop);
        _itemDropRate = getValue("item-drop-rate", DataType.DOUBLE, _itemDropRate);

        _isExpEnabled = getValue("exp-drops-enabled", DataType.BOOLEAN, _isExpEnabled);
        _isDirectExpTransfer = getValue("direct-exp-transfer", DataType.BOOLEAN, _isDirectExpTransfer);
        _expDropRate = getValue("exp-drop-rate", DataType.DOUBLE, _expDropRate);
        _expDropAmount = getValue("exp-drop-amount", DataType.INTEGER, _expDropAmount);
    }



}
