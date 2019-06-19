package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static me.reckfullies.airdrops.gson.adapters.AdapterUtils.jsonArrayToElementList;

public class ItemStackListAdapter implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>>
{
    @Override
    public JsonElement serialize(List<ItemStack> itemStacks, Type type, JsonSerializationContext context)
    {
        JsonArray jsonArray = new JsonArray();
        for (ItemStack item : itemStacks)
        {
            jsonArray.add(
                    context.serialize(item, new TypeToken<ItemStack>(){}.getType())
            );
        }
        return jsonArray;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (JsonElement itemElement : jsonArrayToElementList(jsonElement.getAsJsonArray()))
        {
            itemStacks.add(
                    context.deserialize(itemElement, new TypeToken<ItemStack>(){}.getType())
            );
        }
        return itemStacks;
    }
}
