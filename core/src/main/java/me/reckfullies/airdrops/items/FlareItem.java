package me.reckfullies.airdrops.items;

import me.reckfullies.airdrops.Airdrops;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class FlareItem extends ItemStack
{
    public FlareItem(Airdrops pluginInstance, String packageName)
    {
        super(Material.REDSTONE_TORCH);

        ItemMeta meta = this.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Airdrop Flare");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Spawns 5 airdrops in the surrounding area.");
            lore.add(ChatColor.GRAY + "Package: " + ChatColor.RED + packageName);
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(new NamespacedKey(pluginInstance, "id"), PersistentDataType.BYTE, (byte) 2);
            meta.getPersistentDataContainer().set(new NamespacedKey(pluginInstance, "package"), PersistentDataType.STRING, packageName);

            this.setItemMeta(meta);
        }
    }
}
