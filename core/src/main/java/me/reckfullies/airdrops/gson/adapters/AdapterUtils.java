package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of utilities that are useful for adapters
 *
 * @author Reckfullies
 */
class AdapterUtils
{
    /**
     * Converts a {@link JsonObject} to a {@link Color}
     */
    @NotNull
    static Color jsonToColor(@NotNull JsonObject jsonObject)
    {
        return Color.fromRGB(
                jsonObject.get("red").getAsInt(),
                jsonObject.get("green").getAsInt(),
                jsonObject.get("blue").getAsInt()
        );
    }

    /**
     * Converts a {@link Color} to a {@link JsonObject}
     */
    @NotNull
    static JsonObject colorToJson(@NotNull Color color)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("red", new JsonPrimitive(color.getRed()));
        jsonObject.add("green", new JsonPrimitive(color.getGreen()));
        jsonObject.add("blue", new JsonPrimitive(color.getBlue()));
        return jsonObject;
    }

    /**
     * Converts a {@link JsonArray} to a list of {@link JsonElement}
     */
    @NotNull
    static List<JsonElement> jsonArrayToElementList(@NotNull JsonArray jsonArray)
    {
        List<JsonElement> resultList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
        {
            resultList.add(jsonArray.get(i));
        }
        return resultList;
    }

    /**
     * Converts a {@link JsonArray} to a list of {@link JsonObject}
     */
    @NotNull
    static List<JsonObject> jsonArrayToObjectList(@NotNull JsonArray jsonArray)
    {
        List<JsonObject> resultList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++)
        {
            resultList.add(jsonArray.get(i).getAsJsonObject());
        }
        return resultList;
    }
}
