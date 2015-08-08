package com.jcwhatever.pvs.modules.randombox;

import com.jcwhatever.nucleus.collections.WeightedArrayList;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.items.MatchableItem;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/*
 * 
 */
public class ChestItems {

    private final WeightedArrayList<ItemStack> _items = new WeightedArrayList<>(32);
    private final IDataNode _dataNode;

    public ChestItems(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;
        load();
    }

    public int size() {
        return _items.size();
    }

    public boolean add(ItemStack item, int weight) {
        PreCon.notNull(item);

        //noinspection SuspiciousMethodCalls
        _items.remove(new MatchableItem(item, ItemStackMatcher.getDefault()));
        if (_items.add(item, weight)) {
            save();
            return true;
        }
        return false;
    }

    public boolean remove(ItemStack item) {
        PreCon.notNull(item);

        //noinspection SuspiciousMethodCalls
        if (_items.remove(new MatchableItem(item, ItemStackMatcher.getDefault()))) {
            save();
            return true;
        }
        return false;
    }

    public void clear() {
        _items.clear();
        save();
    }

    public boolean contains(ItemStack item) {
        PreCon.notNull(item);

        //noinspection SuspiciousMethodCalls
        return _items.contains(new MatchableItem(item, ItemStackMatcher.getDefault()));
    }

    @Nullable
    public ItemStack getRandom() {
        if (_items.size() == 0)
            return null;

        return _items.getRandom();
    }

    public WeightedArrayList.WeightedIterator<ItemStack> iterator() {
        return _items.weightedIterator();
    }

    private void save() {
        _dataNode.clear();

        int count = 0;
        WeightedArrayList.WeightedIterator<ItemStack> iterator = _items.weightedIterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            int weight = iterator.weight();

            IDataNode node = _dataNode.getNode("item" + count);
            node.set("item", item);
            node.set("weight", weight);
        }

        _dataNode.save();
    }

    private void load() {

        for (IDataNode node : _dataNode) {
            ItemStack[] items = node.getItemStacks("item");
            int weight = node.getInteger("weight");

            if (items == null || items.length == 0)
                continue;

            _items.add(items[0], weight);
        }
    }
}
