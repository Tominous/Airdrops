package me.reckfullies.airdrops.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.reckfullies.airdrops.Airdrops;
import me.reckfullies.airdrops.Package;
import me.reckfullies.airdrops.PackageIO;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

@CommandAlias("airdrops")
@Description("Main command for Airdrops")
public class PackageCommand extends BaseCommand
{
    @Dependency
    private Airdrops pluginInstance;

    @Dependency
    private PackageIO packageIO;

    @HelpCommand
    private void onHelp(Player player)
    {
        // Print help message
        player.sendMessage(ChatColor.RED + "Help message not implemented!");
    }

    @Subcommand("create")
    @Description("Creates an airdrop with current inventory as rewards - Includes Armor!")
    private void onCreatePackage(Player player, @Single String packageName)
    {
        if (packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' already exists!");
            return;
        }

        ArrayList<ItemStack> itemsToSave = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents())
        {
            if (item != null)
                itemsToSave.add(item);
        }

        Package newPackage = new Package(packageName, itemsToSave);

        packageIO.savePackage(newPackage);
        packageIO.reloadAllPackages();

        player.sendMessage(ChatColor.GREEN + "Package '" + packageName + "' created!");
    }

    @Subcommand("delete")
    @CommandCompletion("@packageName")
    @Description("Deletes an airdrop")
    private void onDeletePackage(Player player, @Single String packageName)
    {
        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        // TODO: Incomplete for now
    }

    @Subcommand("call")
    @CommandCompletion("@packageName")
    @Description("Calls an airdrop where the player is looking")
    private void onCallPackage(Player player, @Single String packageName)
    {
        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        Package loadedPackage = packageIO.loadPackage(packageName);

        Block targetBlock = player.getTargetBlock(null, 6).getRelative(BlockFace.UP);
        if (targetBlock.getType() == Material.AIR)
        {
            targetBlock.setType(Material.CHEST);
            Chest c = (Chest) targetBlock.getState();

            // Set persistent data
            c.getPersistentDataContainer().set(new NamespacedKey(pluginInstance, "airdrops"), PersistentDataType.BYTE, (byte) 1);
            c.update();

            // Set inventory
            ItemStack[] contents = new ItemStack[0];
            c.getBlockInventory().setContents(loadedPackage.getItems().toArray(contents));
        }
        else
        {
            player.sendMessage(ChatColor.RED + "Invalid Location!");
        }
    }

    @Subcommand("reload")
    private void onReloadPackages(Player player)
    {
        packageIO.reloadAllPackages();
        player.sendMessage(ChatColor.GREEN + "Packages Reloaded");
    }
}
