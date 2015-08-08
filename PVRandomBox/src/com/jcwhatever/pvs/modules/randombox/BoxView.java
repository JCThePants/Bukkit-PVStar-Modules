package com.jcwhatever.pvs.modules.randombox;

import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.chest.ChestEventAction;
import com.jcwhatever.nucleus.views.chest.ChestEventInfo;
import com.jcwhatever.nucleus.views.chest.ChestView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/*
 * 
 */
public class BoxView extends ChestView {

    private final RandomBoxExtension _extension;

    public BoxView(Plugin plugin, RandomBoxExtension extension) {
        super(plugin, null);

        _extension = extension;
    }

    @Override
    public String getTitle() {
        return "Random Box";
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        // do nothing
    }

    @Override
    protected Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(getPlayer(), 9);

        ItemStack item = _extension.getItems().getRandom();

        if (item != null) {
            inventory.setItem(0, item);
        }

        return inventory;
    }

    @Override
    protected ChestEventAction onItemsPlaced(ChestEventInfo eventInfo) {
        return ChestEventAction.ALLOW;
    }

    @Override
    protected ChestEventAction onItemsPickup(ChestEventInfo eventInfo) {
        return ChestEventAction.ALLOW;
    }

    @Override
    protected ChestEventAction onItemsDropped(ChestEventInfo eventInfo) {
        return ChestEventAction.DENY;
    }
}
