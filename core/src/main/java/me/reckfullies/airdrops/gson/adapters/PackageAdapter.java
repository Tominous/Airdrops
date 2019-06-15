package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.reckfullies.airdrops.Package;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PackageAdapter implements JsonSerializer<Package>, JsonDeserializer<Package>
{
    @Override
    public JsonElement serialize(Package pkg, Type type, JsonSerializationContext context)
    {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add(
                "packageName",
                new JsonPrimitive(pkg.getName())
        );
        jsonObject.add(
                "packageItems",
                context.serialize(pkg.getItems(), new TypeToken<List<ItemStack>>(){}.getType())
        );

        return jsonObject;
    }

    @Override
    public Package deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String packageName = "";
        List<ItemStack> packageItems = new ArrayList<>();

        if (jsonObject.has("packageName"))
            packageName = jsonObject.get("packageName").getAsString();

        if (jsonObject.has("packageItems"))
            packageItems = context.deserialize(
                    jsonObject.get("packageItems"),
                    new TypeToken<List<ItemStack>>(){}.getType()
            );

        return new Package(packageName, packageItems);
    }
}
