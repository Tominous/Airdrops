package me.reckfullies.airdrops.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.reckfullies.airdrops.Package;
import me.reckfullies.airdrops.PackageIO;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("package|pkg")
@Description("Main command for Airdrops")
public class PackageCommand extends BaseCommand
{
    @Dependency
    private PackageIO packageIO;

    @HelpCommand
    private void onHelp(Player player)
    {
        // Print help message
        player.sendMessage(ChatColor.RED + "Help message not implemented!");
    }

    @Subcommand("give")
    @CommandCompletion("@packageName")
    private void onGivePackage(Player player, @Single String packageName)
    {
        if (!packageIO.checkPackageExists(packageName))
        {
            player.sendMessage(ChatColor.RED + "Package '" + packageName + "' does not exist!");
            return;
        }

        Package loadedPackage = packageIO.loadPackage(packageName);

        player.sendMessage(new String[]{
                ChatColor.GREEN + "-------- Package Info --------",
                ChatColor.YELLOW + "Name: " + ChatColor.RED + loadedPackage.getName(),
                ChatColor.YELLOW + "Type: " + ChatColor.RED + loadedPackage.getType(),
                ChatColor.GREEN + "------------------------------"
        });
    }

    @Subcommand("reload")
    private void onReloadPackages(Player player)
    {
        packageIO.reloadAllPackages();
        player.sendMessage(ChatColor.GREEN + "Packages Reloaded");
    }
}
