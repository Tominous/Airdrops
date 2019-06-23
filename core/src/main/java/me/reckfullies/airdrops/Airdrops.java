package me.reckfullies.airdrops;

import co.aikar.commands.PaperCommandManager;
import me.reckfullies.airdrops.commands.PackageCommand;
import me.reckfullies.airdrops.listeners.ChestListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry-point for Airdrops
 *
 * @author Reckfullies
 */
public final class Airdrops extends JavaPlugin
{
    private Metrics metrics;
    private PackageIO packageIO;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable()
    {
        metrics = new Metrics(this);
        packageIO = new PackageIO(this, getDataFolder().getAbsolutePath());

        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

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
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new ChestListener(this), this);
    }

    //region Getters
    public PackageIO getPackageIO()
    {
        return packageIO;
    }
    //endregion
}
