package me.reckfullies.airdrops;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.reckfullies.airdrops.gson.adapters.PackageAdapter;
import me.reckfullies.airdrops.gson.adapters.PackageListAdapter;
import me.reckfullies.jsonlib.api.JSONLib;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving/loading of {@link Package} data
 *
 * @author Reckfullies
 */
public class PackageIO
{
    private String pluginDataPath;
    private String packageJsonPath;

    private JSONLib jsonLib;
    private List<Package> loadedPackages;

    PackageIO(Airdrops pluginInstance, String pluginDataPath)
    {
        this.pluginDataPath = pluginDataPath;
        this.packageJsonPath = pluginDataPath.concat("\\packages.json");

        this.jsonLib = new JSONLib(pluginInstance);
        this.jsonLib.addAdapter(new TypeToken<Package>(){}, new PackageAdapter());
        this.jsonLib.addAdapter(new TypeToken<List<Package>>(){}, new PackageListAdapter());
        this.jsonLib.init(true);

        this.loadedPackages = loadAllPackages();
    }

    /**
     * Saves a {@link Package} to JSON
     */
    public void savePackage(@NotNull Package packageToSave)
    {
        List<Package> packageList = readPackagesJson();

        if (!packageList.contains(packageToSave))
            packageList.add(packageToSave);

        writePackagesJson(packageList);
    }

    /**
     * Deletes a {@link Package} from JSON
     */
    public void deletePackage(@NotNull String packageName)
    {
        List<Package> packageList = readPackagesJson();

        packageList.removeIf(pkg -> pkg.getName().equals(packageName));

        writePackagesJson(packageList);
    }

    /**
     * Loads a {@link Package} from memory
     */
    @NotNull
    public Package loadPackage(@NotNull String packageName)
    {
        for (Package pkg : loadedPackages)
        {
            if (pkg.getName().equals(packageName))
                return pkg;
        }

        throw new RuntimeException("Failed to load package '" + packageName + "' - package does not exist!");
    }

    /**
     * Reloads {@link Package} list currently stored in memory
     */
    public void reloadAllPackages()
    {
        this.loadedPackages = loadAllPackages();
    }

    /**
     * Checks for a {@link Package} in memory
     */
    public boolean checkPackageExists(@NotNull String packageName)
    {
        for (Package pkg : loadedPackages)
        {
            if (pkg.getName().equals(packageName))
                return true;
        }

        return false;
    }

    /**
     * Loads a list of {@link Package} from JSON
     */
    @NotNull
    private List<Package> loadAllPackages()
    {
        File jsonFile = new File(packageJsonPath);

        if (jsonFile.exists())
            return readPackagesJson();
        else
            return new ArrayList<>();
    }

    /**
     * Reads a list of {@link Package} from a JSON file
     */
    @NotNull
    private List<Package> readPackagesJson()
    {
        File jsonFile = new File(packageJsonPath);
        if (!jsonFile.exists())
            return new ArrayList<>();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(packageJsonPath));
            StringBuilder jsonBuilder = new StringBuilder();

            String currentLine;
            while ((currentLine = br.readLine()) != null)
                jsonBuilder.append(currentLine);

            return jsonLib.deserializeGeneric(new TypeToken<List<Package>>(){}, jsonBuilder.toString());
        }
        catch (IOException | JsonIOException | JsonSyntaxException ex)
        {
            if (ex instanceof JsonSyntaxException)
            {
                throw new RuntimeException("Failed to read packages.json! - Invalid JSON Syntax\n" + ex.getMessage());
            }
            else
                throw new RuntimeException("Failed to read packages.json! - IO Exception/File Not Found", ex);
        }
    }

    /**
     * Writes a list of {@link Package} to a JSON file
     */
    private void writePackagesJson(@NotNull List<Package> packages)
    {
        try
        {
            File directory = new File(pluginDataPath);
            if (!directory.exists())
                directory.mkdir();

            Writer writer = new FileWriter(packageJsonPath);
            writer.write(jsonLib.serializeGeneric(new TypeToken<List<Package>>(){}, packages));
            writer.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to write to packages.json!", ex);
        }
    }

    //region Getters
    public List<Package> getLoadedPackages()
    {
        return loadedPackages;
    }
    public List<String> getLoadedPackageNames()
    {
        List<String> packageNames = new ArrayList<>();

        for (Package pkg : loadedPackages)
            packageNames.add(pkg.getName());

        return packageNames;
    }
    //endregion
}
