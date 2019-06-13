package me.reckfullies.airdrops;

public class Package
{
    private String packageName;
    private PackageType packageType;

    public Package(String packageName, PackageType packageType)
    {
        this.packageName = packageName;
        this.packageType = packageType;
    }

    //region Getters
    public String getName()
    {
        return packageName;
    }
    public PackageType getType()
    {
        return packageType;
    }
    //endregion
}
