package me.reckfullies.airdrops.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.reckfullies.airdrops.Airdrops;
import me.reckfullies.airdrops.Package;
import me.reckfullies.airdrops.PackageIO;
import me.reckfullies.airdrops.items.SignalItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

@CommandAlias("airdrops")
@CommandPermission("airdrops")
@Description("Main command for Airdrops")
public class PackageCommand extends BaseCommand
{
    @Dependency
    private Airdrops pluginInstance;

    @Dependency
    private PackageIO packageIO;

    @HelpCommand
    private void onHelp(CommandSender sender, CommandHelp help)
    {
        help.showHelp();
    }

    @Subcommand("create")
    @CommandPermission("airdrops.create")
    @Description("Creates an airdrop with current inventory as rewards.")
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
    @CommandPermission("airdrops.delete")
    @Description("Deletes an airdrop.")
    private void onDeletePackage(Player player, @Single String packageName)
    {
        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        packageIO.deletePackage(packageName);
        packageIO.reloadAllPackages();
        player.sendMessage(ChatColor.GREEN + "Package '" + packageName + "' deleted!");
    }

    @Subcommand("signal")
    @CommandCompletion("@packageName")
    @CommandPermission("airdrops.signal")
    @Description("Gives an item which can call a single airdrop")
    private void onGiveSignal(Player player, @Single String packageName)
    {
        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        player.getInventory().addItem(new SignalItem(pluginInstance, packageName));
        player.sendMessage(ChatColor.GREEN + "You have been given a Signal!");
    }

    @Subcommand("reload")
    @CommandPermission("airdrops.reload")
    @Description("Reload all packages from file.")
    private void onReloadPackages(Player player)
    {
        packageIO.reloadAllPackages();
        player.sendMessage(ChatColor.GREEN + "Packages reloaded");
    }
}
