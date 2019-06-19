package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.reckfullies.airdrops.Package;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static me.reckfullies.airdrops.gson.adapters.AdapterUtils.jsonArrayToElementList;

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
        List<Package> packages = new ArrayList<>();
        for (JsonElement packageElement : jsonArrayToElementList(jsonElement.getAsJsonArray()))
        {
            packages.add(
                    context.deserialize(packageElement, new TypeToken<Package>(){}.getType())
            );
        }
        return packages;
    }
}
