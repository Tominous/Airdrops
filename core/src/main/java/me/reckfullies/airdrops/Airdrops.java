package me.reckfullies.airdrops;

import co.aikar.commands.PaperCommandManager;
import me.reckfullies.airdrops.commands.PackageCommand;
import me.reckfullies.airdrops.listeners.ChestListener;
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

        RegisterListeners();
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
        commandManager.getCommandCompletions().registerCompletion("packageName", c -> packageIO.getLoadedPackageNames());
    }

    /**
     * Registers commands for ACF
     */
    private void RegisterCommands()
    {
        commandManager.registerCommand(new PackageCommand());
    }

    /**
     * Registers event listeners
     */
    private void RegisterListeners()
    {
        getServer().getPluginManager().registerEvents(new ChestListener(this), this);
    }

    //region Getters
    public PackageIO getPackageIO()
    {
        return packageIO;
    }
    //endregion
}
