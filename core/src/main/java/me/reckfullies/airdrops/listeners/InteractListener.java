package me.reckfullies.airdrops.listeners;

import com.sun.org.apache.xml.internal.utils.NameSpace;
import me.reckfullies.airdrops.Airdrops;
import me.reckfullies.airdrops.Package;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class InteractListener implements Listener
{
    private Airdrops pluginInstance;

    public InteractListener(Airdrops pluginInstance)
    {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    private void onRightClick(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            ItemStack item = event.getItem();
            if (item == null)
                return;

            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;

            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            NamespacedKey idKey = new NamespacedKey(pluginInstance, "id");
            NamespacedKey packageNameKey = new NamespacedKey(pluginInstance, "package");

            // Signal
            if (dataContainer.has(idKey, PersistentDataType.BYTE))
            {
                //noinspection ConstantConditions
                if (dataContainer.get(idKey, PersistentDataType.BYTE).equals((byte) 1))
                {
                    // Signal
                    String packageName = "";
                    if (dataContainer.has(packageNameKey, PersistentDataType.STRING))
                        packageName = dataContainer.get(packageNameKey, PersistentDataType.STRING);

                    Package itemPackage = null;
                    if (pluginInstance.getPackageIO().checkPackageExists(packageName))
                        itemPackage = pluginInstance.getPackageIO().loadPackage(packageName);

                    if (itemPackage != null)
                    {
                        event.getPlayer().getInventory().remove(item);
                        event.getPlayer().sendMessage("Called Package! - " + itemPackage.getName() + " - " + itemPackage.getItems().size());
                    }
                }
                else
                {
                    // TODO: Flare
                }

                event.setCancelled(true);
            }
        }
    }
}
