package me.reckfullies.airdrops;

import co.aikar.commands.PaperCommandManager;
import me.reckfullies.airdrops.commands.PackageCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Airdrops extends JavaPlugin
{
    private Metrics metrics;
    private PackageIO packageIO;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable()
    {
        metrics = new Metrics(this);
        commandManager = new PaperCommandManager(this);
        packageIO = new PackageIO(getDataFolder().getAbsolutePath());

        RegisterDependencies();
        RegisterCompletions();
        RegisterCommands();
    }

    /**
     * Registers command dependencies for ACF
     */
    private void RegisterDependencies()
    {
        commandManager.registerDependency(PackageIO.class, packageIO);
    }

    /**
     * Registers command tab-completions for ACF
     */
    private void RegisterCompletions()
    {
        commandManager.getCommandCompletions().registerCompletion("packageName", c -> packageIO.getLoadedPackages().keySet());
    }

    /**
     * Registers commands for ACF
     */
    private void RegisterCommands()
    {
        commandManager.registerCommand(new PackageCommand());
    }

    //region Getters
    public PackageIO getPackageIO()
    {
        return packageIO;
    }
    //endregion
}
