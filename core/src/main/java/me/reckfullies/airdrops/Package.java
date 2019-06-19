package me.reckfullies.airdrops;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Container that holds information about an airdrop configuration
 *
 * @author Reckfullies
 */
public class Package
{
    private String packageName;
    private List<ItemStack> packageItems;

    public Package(String packageName, List<ItemStack> packageItems)
    {
        this.packageName = packageName;
        this.packageItems = packageItems;
    }

    //region Getters
    public String getName()
    {
        return packageName;
    }
    public List<ItemStack> getItems() { return packageItems; }
    //endregion
}
