package me.reckfullies.airdrops.listeners;

import me.reckfullies.airdrops.Airdrops;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for all chest related events
 *
 * @author Reckfullies
 */
public class ChestListener implements Listener
{
    private Airdrops pluginInstance;

    public ChestListener(Airdrops pluginInstance)
    {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onChestEmpty(InventoryCloseEvent event)
    {
        Inventory inv = event.getInventory();

        if (!(inv.getHolder() instanceof Chest))
            return;

        if (isInventoryEmpty(inv))
        {
            Chest c = (Chest) inv.getHolder();

            if (c == null)
                return;

            // Check if chest is an airdrop
            if (c.getPersistentDataContainer().has(new NamespacedKey(pluginInstance, "airdrops"), PersistentDataType.BYTE))
                c.getBlock().setType(Material.AIR);
        }
    }

    /**
     * Checks if an {@link Inventory} is empty
     */
    private boolean isInventoryEmpty(@NotNull Inventory inv)
    {
        for (ItemStack item : inv.getContents())
        {
            if (item != null)
                return false;
        }

        return true;
    }
}
