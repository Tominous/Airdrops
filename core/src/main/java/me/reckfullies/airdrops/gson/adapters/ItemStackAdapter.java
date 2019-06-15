package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>
{
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context)
    {
        if (itemStack == null)
            return null;

        JsonObject jsonObject = new JsonObject();

        jsonObject.add("material", new JsonPrimitive(itemStack.getType().name()));

        if (itemStack.hasItemMeta())
        {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasDisplayName())
                jsonObject.add("displayName", new JsonPrimitive(itemMeta.getDisplayName()));

            if (itemMeta.hasLore())
            {
                JsonArray loreArray = new JsonArray();
                for (String loreElement : itemMeta.getLore())
                {
                    loreArray.add(new JsonPrimitive(loreElement));
                }
                jsonObject.add("lore", loreArray);
            }

            if (itemMeta.hasEnchants())
            {
                jsonObject.add("enchantments", new JsonPrimitive(serializeEnchantments(itemMeta.getEnchants())));
            }

            if (itemMeta instanceof Damageable)
            {
                Damageable damageable = (Damageable) itemMeta;

                if (damageable.hasDamage())
                {
                    jsonObject.add("durability", new JsonPrimitive(damageable.getDamage()));
                }
            }
        }

        return jsonObject;
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        ItemStack itemStack = new ItemStack(Material.STONE);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (jsonObject.has("material"))
        {
            Material itemMaterial = Material.getMaterial(jsonObject.get("material").getAsString());
            if (itemMaterial != null)
                itemStack.setType(itemMaterial);
        }
        if (jsonObject.has("displayName"))
            itemMeta.setDisplayName(jsonObject.get("displayName").getAsString());
        if (jsonObject.has("lore"))
        {
            List<String> loreList = new ArrayList<>();
            JsonArray loreArray = jsonObject.getAsJsonArray("lore");
            for (int j = 0; j < loreArray.size(); j++)
            {
                loreList.add(loreArray.get(j).getAsString());
            }
            itemMeta.setLore(loreList);
        }
        if (jsonObject.has("enchantments"))
        {
            Map<Enchantment, Integer> enchants = deserializeEnchantments(jsonObject.get("enchantments").getAsString());

            for (Enchantment enchant : enchants.keySet())
            {
                itemMeta.addEnchant(enchant, enchants.get(enchant), false);
            }
        }
        if (jsonObject.has("durability"))
        {
            Damageable damageable = (Damageable) itemMeta;
            damageable.setDamage(jsonObject.get("durability").getAsInt());
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private String serializeEnchantments(Map<Enchantment, Integer> enchantments)
    {
        StringBuilder serialized = new StringBuilder();
        for (Enchantment enchant : enchantments.keySet())
        {
            serialized.append(enchant.getName()).append(":").append(enchantments.get(enchant)).append(";");
        }
        return serialized.toString();
    }

    private Map<Enchantment, Integer> deserializeEnchantments(String serializedEnchantments)
    {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();

        if (serializedEnchantments.isEmpty())
            return enchantments;

        String[] enchants = serializedEnchantments.split(";");
        for (String e : enchants)
        {
            String[] enchant = e.split(":");
            enchantments.put(Enchantment.getByName(enchant[0]), Integer.parseInt(enchant[1]));
        }

        return enchantments;
    }
}
