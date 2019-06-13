package me.reckfullies.airdrops;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Airdrops extends JavaPlugin
{
    private PackageIO packageIO;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        this.packageIO = new PackageIO(this.getDataFolder().getAbsolutePath());

        Package testPackage = this.packageIO.loadPackage("createdInConfig");

        if (testPackage != null)
        {
            this.getLogger().log(Level.INFO, "----- Package Info -----");
            this.getLogger().log(Level.INFO, "Name: " + testPackage.getName());
            this.getLogger().log(Level.INFO, "Type: " + testPackage.getType());
            this.getLogger().log(Level.INFO, "------------------------");
        }
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}
