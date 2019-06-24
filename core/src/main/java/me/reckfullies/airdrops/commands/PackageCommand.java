package me.reckfullies.airdrops.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.reckfullies.airdrops.Airdrops;
import me.reckfullies.airdrops.Package;
import me.reckfullies.airdrops.PackageIO;
import me.reckfullies.airdrops.items.FlareItem;
import me.reckfullies.airdrops.items.SignalItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    @Subcommand("flare")
    @CommandCompletion("@packageName")
    @CommandPermission("airdrops.flare")
    @Description("Gives an item which can call 5 airdrops in a 5x5 chunk radius.")
    private void onGiveFlare(Player player, @Single String packageName, @Optional Player otherPlayer)
    {
        Player targetPlayer = otherPlayer == null ? player : otherPlayer;

        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        targetPlayer.getInventory().addItem(new FlareItem(pluginInstance, packageName));
        targetPlayer.sendMessage(ChatColor.GREEN + "You have been given a Flare!");

        if (targetPlayer != player)
        {
            player.sendMessage(ChatColor.GREEN + "You have given a Flare to " + targetPlayer.getName());
        }
    }

    @Subcommand("signal")
    @CommandCompletion("@packageName")
    @CommandPermission("airdrops.signal")
    @Description("Gives an item which can call a single airdrop")
    private void onGiveSignal(Player player, @Single String packageName, @Optional Player otherPlayer)
    {
        Player targetPlayer = otherPlayer == null ? player : otherPlayer;

        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        targetPlayer.getInventory().addItem(new SignalItem(pluginInstance, packageName));
        targetPlayer.sendMessage(ChatColor.GREEN + "You have been given a Signal!");

        if (targetPlayer != player)
        {
            player.sendMessage(ChatColor.GREEN + "You have given a Signal to " + targetPlayer.getName());
        }
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
