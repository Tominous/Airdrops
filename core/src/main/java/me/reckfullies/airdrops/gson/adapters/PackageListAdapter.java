package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.reckfullies.airdrops.Package;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PackageListAdapter implements JsonSerializer<List<Package>>, JsonDeserializer<List<Package>>
{
    @Override
    public JsonElement serialize(List<Package> packages, Type type, JsonSerializationContext context)
    {
        JsonArray jsonArray = new JsonArray();

        for (Package pkg : packages)
        {
            jsonArray.add(
                    context.serialize(pkg, new TypeToken<Package>(){}.getType())
            );
        }

        return jsonArray;
    }

    @Override
    public List<Package> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Package> packages = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++)
        {
            packages.add(
                    context.deserialize(jsonArray.get(i), new TypeToken<Package>(){}.getType())
            );
        }

        return packages;
    }
}
