package me.reckfullies.airdrops;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

/**
 * Handles saving/loading of package data
 *
 * @author Reckfullies
 */
class PackageIO
{
    private String pluginDataPath;
    private String packageJsonPath;

    private Gson gson;
    private HashMap<String, Package> loadedPackages;

    PackageIO(String pluginDataPath)
    {
        this.pluginDataPath = pluginDataPath;
        this.packageJsonPath = pluginDataPath.concat("\\packages.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.loadedPackages = loadAllPackages();
    }

    /**
     * Saves a package configuration to JSON
     *
     * @param packageToSave Package object to save
     */
    void savePackage(Package packageToSave)
    {
        File jsonFile = new File(packageJsonPath);
        HashMap<String, Package> packageMap = new HashMap<>();

        if (jsonFile.exists())
            packageMap = readPackagesJson();

        if (packageMap == null)
            packageMap = new HashMap<>();

        if (!packageMap.containsKey(packageToSave.getName()))
            packageMap.put(packageToSave.getName(), packageToSave);

        try
        {
            File directory = new File(pluginDataPath);
            if (!directory.exists())
                directory.mkdir();

            Writer writer = new FileWriter(packageJsonPath);
            writer.write(gson.toJson(packageMap));
            writer.close();
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to write to packages.json!", ex);
        }
    }

    /**
     * Loads a package configuration from memory
     *
     * @param packageName Package name to load
     * @return Package object generated from JSON, some values may be null
     */
    Package loadPackage(String packageName)
    {
        if (loadedPackages.containsKey(packageName))
        {
            return loadedPackages.get(packageName);
        }
        else
        {
            throw new RuntimeException("Failed to load package '" + packageName + "' - package does not exist!");
        }
    }

    /**
     * Loads all package configurations from JSON
     *
     * @return Package map generated from JSON, some values may be null
     */
    @Nullable
    private HashMap<String, Package> loadAllPackages()
    {
        File jsonFile = new File(packageJsonPath);

        if (jsonFile.exists())
            return readPackagesJson();
        else
            throw new RuntimeException("Failed to load packages - packages.json does not exist!");
    }

    /**
     * Read packages from a JSON file
     *
     * @return Packages map if found, otherwise will return null
     */
    @Nullable
    private HashMap<String, Package> readPackagesJson()
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(packageJsonPath));
            return gson.fromJson(br, new TypeToken<HashMap<String, Package>>() {}.getType());
        }
        catch (FileNotFoundException | JsonIOException | JsonSyntaxException ex)
        {
            if (ex instanceof JsonSyntaxException)
            {
                throw new RuntimeException("Failed to read packages.json! - Invalid JSON Syntax\n" + ex.getMessage());
            }
            else
                throw new RuntimeException("Failed to read packages.json! - IO Exception/File Not Found", ex);
        }
    }
}
