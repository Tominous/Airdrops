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

public class SignalItem extends ItemStack
{
    public SignalItem(Airdrops pluginInstance, String packageName)
    {
        super(Material.TORCH);

        ItemMeta meta = this.getItemMeta();
        if (meta != null)
        {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Airdrop Signal");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Spawns a single airdrop at right-click position.");
            lore.add(ChatColor.GRAY + "Package: " + ChatColor.YELLOW + packageName);
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(new NamespacedKey(pluginInstance, "id"), PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(new NamespacedKey(pluginInstance, "package"), PersistentDataType.STRING, packageName);

            this.setItemMeta(meta);
        }
    }
}
