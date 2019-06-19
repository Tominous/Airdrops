package me.reckfullies.airdrops.gson.adapters;

import com.google.gson.*;
import me.reckfullies.airdrops.Airdrops;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.reckfullies.airdrops.gson.adapters.AdapterUtils.*;

/**
 * GSON serializer/deserializer for {@link ItemStack}
 *
 * @author Reckfullies
 */
public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack>
{
    private Airdrops pluginInstance;

    public ItemStackAdapter(Airdrops pluginInstance)
    {
        this.pluginInstance = pluginInstance;
    }


    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context)
    {
        if (itemStack == null)
            return null;

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("amount", new JsonPrimitive(itemStack.getAmount()));
        jsonObject.add("material", new JsonPrimitive(itemStack.getType().name()));

        if (itemStack.hasItemMeta() && itemStack.getItemMeta() != null)
        {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasDisplayName())
                jsonObject.add("displayName", new JsonPrimitive(itemMeta.getDisplayName()));

            if (itemMeta.hasLore() && itemMeta.getLore() != null)
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
                jsonObject.add("enchantments", serializeEnchantments(itemMeta.getEnchants()));
            }

            if (itemMeta instanceof Damageable)
            {
                Damageable damageable = (Damageable) itemMeta;

                if (damageable.hasDamage())
                {
                    jsonObject.add("durability", new JsonPrimitive(damageable.getDamage()));
                }
            }

            if (itemMeta instanceof PotionMeta)
            {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                jsonObject.add("potionMeta", serializePotion(potionMeta));
            }

            if (itemMeta instanceof EnchantmentStorageMeta)
            {
                EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) itemMeta;
                jsonObject.add("enchantStorageMeta", serializeEnchantStorage(enchantMeta));
            }

            if (itemMeta instanceof BookMeta)
            {
                BookMeta bookMeta = (BookMeta) itemMeta;
                jsonObject.add("bookMeta", serializeBook(bookMeta));
            }

            if (itemMeta instanceof FireworkMeta)
            {
                FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;
                jsonObject.add("fireworkMeta", serializeFirework(fireworkMeta));
            }

            if (itemMeta instanceof LeatherArmorMeta)
            {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;
                jsonObject.add("leatherArmorMeta", serializeLeatherArmor(leatherArmorMeta));
            }

            if (itemMeta instanceof SkullMeta)
            {
                SkullMeta skullMeta = (SkullMeta) itemMeta;
                jsonObject.add("skullMeta", serializeSkull(skullMeta));
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

        if (itemMeta == null)
            return itemStack;

        if (jsonObject.has("amount"))
        {
            itemStack.setAmount(jsonObject.get("amount").getAsInt());
        }

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
            for (JsonElement loreElement: jsonArrayToElementList(jsonObject.get("lore").getAsJsonArray()))
            {
                loreList.add(loreElement.getAsString());
            }
            itemMeta.setLore(loreList);
        }

        if (jsonObject.has("enchantments"))
        {
            Map<Enchantment, Integer> enchants = deserializeEnchantments(jsonObject.get("enchantments").getAsJsonArray());

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

        if (jsonObject.has("potionMeta"))
            itemMeta = deserializePotion(jsonObject.get("potionMeta").getAsJsonObject());

        if (jsonObject.has("enchantStorageMeta"))
            itemMeta = deserializeEnchantStorage(jsonObject.get("enchantStorageMeta").getAsJsonArray());

        if (jsonObject.has("bookMeta"))
            itemMeta = deserializeBook(jsonObject.get("bookMeta").getAsJsonObject());

        if (jsonObject.has("fireworkMeta"))
            itemMeta = deserializeFirework(jsonObject.get("fireworkMeta").getAsJsonObject());

        if (jsonObject.has("leatherArmorMeta"))
            itemMeta = deserializeLeatherArmor(jsonObject.get("leatherArmorMeta").getAsJsonObject());

        if (jsonObject.has("skullMeta"))
            itemMeta = deserializeSkull(jsonObject.get("skullMeta").getAsJsonObject());

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


    //region Enchantments
    /**
     * Serialize an {@link Enchantment} map into a {@link JsonArray}
     */
    @NotNull
    private JsonArray serializeEnchantments(@NotNull Map<Enchantment, Integer> enchantments)
    {
        JsonArray enchantArray = new JsonArray();
        for (Enchantment enchant : enchantments.keySet())
        {
            JsonObject enchantObject = new JsonObject();
            enchantObject.add("type", new JsonPrimitive(enchant.getName()));
            enchantObject.add("amplifier", new JsonPrimitive(enchantments.get(enchant)));
            enchantArray.add(enchantObject);
        }
        return enchantArray;
    }

    /**
     * Deserialize a {@link JsonArray} into an {@link Enchantment} map
     */
    @NotNull
    private Map<Enchantment, Integer> deserializeEnchantments(@NotNull JsonArray jsonArray)
    {
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        for (JsonObject enchantObject : jsonArrayToObjectList(jsonArray))
        {
            Integer enchantAmplifier = enchantObject.get("amplifier").getAsInt();
            Enchantment enchant = Enchantment.getByName(enchantObject.get("type").getAsString());
            if (enchant == null)
                throw new RuntimeException("Error serializing item! - Invalid Enchantment: '" + enchantObject.get("type").getAsString() + "'");

            enchantments.put(enchant, enchantAmplifier);
        }
        return enchantments;
    }
    //endregion


    //region Potion/Tipped Arrow
    /**
     * Serialize a {@link PotionMeta} into a {@link JsonObject}
     */
    @NotNull
    private JsonObject serializePotion(@NotNull PotionMeta potionMeta)
    {
        JsonObject jsonObject = new JsonObject();

        PotionData potionData = potionMeta.getBasePotionData();
        jsonObject.add("type", new JsonPrimitive(potionData.getType().toString()));
        jsonObject.add("upgraded", new JsonPrimitive(potionData.isUpgraded()));
        jsonObject.add("extended", new JsonPrimitive(potionData.isExtended()));

        if (potionMeta.hasColor())
        {
            Color potionColor = potionMeta.getColor();
            if (potionColor != null)
                jsonObject.add("color", colorToJson(potionColor));
        }

        if (potionMeta.hasCustomEffects())
        {
            JsonArray effectArray = new JsonArray();
            for (PotionEffect effect : potionMeta.getCustomEffects())
            {
                JsonObject effectObject = new JsonObject();
                effectObject.add("type", new JsonPrimitive(effect.getType().getName()));
                effectObject.add("amplifier", new JsonPrimitive(effect.getAmplifier()));
                effectObject.add("duration", new JsonPrimitive(effect.getDuration()));
                effectArray.add(effectObject);
            }
            jsonObject.add("customEffects", effectArray);
        }

        return jsonObject;
    }

    /**
     * Deserialize a {@link JsonObject} into a {@link PotionMeta}
     */
    @NotNull
    private PotionMeta deserializePotion(@NotNull JsonObject jsonObject)
    {
        PotionMeta potionMeta = (PotionMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.POTION);
        if (potionMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to PotionMeta");

        PotionData potionData = new PotionData(
                PotionType.valueOf(jsonObject.get("type").getAsString()),
                jsonObject.get("extended").getAsBoolean(),
                jsonObject.get("upgraded").getAsBoolean()
        );
        potionMeta.setBasePotionData(potionData);

        if (jsonObject.has("color"))
        {
            Color potionColor = jsonToColor(jsonObject.get("color").getAsJsonObject());
            potionMeta.setColor(potionColor);
        }

        if (jsonObject.has("customEffects"))
        {
            for (JsonObject effectObject : jsonArrayToObjectList(jsonObject.get("customEffects").getAsJsonArray()))
            {
                PotionEffectType potionEffectType = PotionEffectType.getByName(effectObject.get("type").getAsString());
                if (potionEffectType == null)
                    throw new RuntimeException("Error serializing item! - Invalid Potion Type: '" + effectObject.get("type").getAsString() + "'");

                PotionEffect potionEffect = new PotionEffect(
                        potionEffectType,
                        effectObject.get("duration").getAsInt(),
                        effectObject.get("amplifier").getAsInt()
                );

                potionMeta.addCustomEffect(potionEffect, true);
            }
        }

        return potionMeta;
    }
    //endregion


    //region Enchantment Storage
    /**
     * Serialize an {@link EnchantmentStorageMeta} into a {@link JsonArray}
     */
    @NotNull
    private JsonArray serializeEnchantStorage(@NotNull EnchantmentStorageMeta enchantMeta)
    {
        JsonArray jsonArray = new JsonArray();

        if (enchantMeta.hasStoredEnchants())
            jsonArray = serializeEnchantments(enchantMeta.getStoredEnchants());

        return jsonArray;
    }

    /**
     * Deserialize a {@link JsonArray} into an {@link EnchantmentStorageMeta}
     */
    @NotNull
    private EnchantmentStorageMeta deserializeEnchantStorage(@NotNull JsonArray jsonArray)
    {
        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
        if (enchantMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to EnchantmentStorageMeta");

        if (jsonArray.size() != 0)
        {
            Map<Enchantment, Integer> enchantMap = deserializeEnchantments(jsonArray);
            for (Enchantment enchant : enchantMap.keySet())
            {
                enchantMeta.addStoredEnchant(enchant, enchantMap.get(enchant), false);
            }
        }

        return enchantMeta;
    }
    //endregion


    //region Book
    /**
     * Serialize a {@link BookMeta} into a {@link JsonObject}
     */
    @NotNull
    private JsonObject serializeBook(@NotNull BookMeta bookMeta)
    {
        JsonObject jsonObject = new JsonObject();

        if (bookMeta.hasTitle() && bookMeta.getTitle() != null)
            jsonObject.add("title", new JsonPrimitive(bookMeta.getTitle()));

        if (bookMeta.hasAuthor() && bookMeta.getAuthor() != null)
            jsonObject.add("author", new JsonPrimitive(bookMeta.getAuthor()));

        if (bookMeta.hasGeneration() && bookMeta.getGeneration() != null)
            jsonObject.add("generation", new JsonPrimitive(bookMeta.getGeneration().toString()));

        if (bookMeta.hasPages())
        {
            JsonArray jsonArray = new JsonArray();
            for (String page : bookMeta.getPages())
            {
                jsonArray.add(new JsonPrimitive(page));
            }
            jsonObject.add("pages", jsonArray);
        }

        return jsonObject;
    }

    /**
     * Deserialize a {@link JsonObject} into a {@link BookMeta}
     */
    @NotNull
    private BookMeta deserializeBook(@NotNull JsonObject jsonObject)
    {
        BookMeta bookMeta = (BookMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
        if (bookMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to BookMeta");

        if (jsonObject.has("title"))
            bookMeta.setTitle(jsonObject.get("title").getAsString());

        if (jsonObject.has("author"))
            bookMeta.setAuthor(jsonObject.get("author").getAsString());

        if (jsonObject.has("generation"))
        {
            try
            {
                BookMeta.Generation generation = BookMeta.Generation.valueOf(jsonObject.get("generation").getAsString());
                bookMeta.setGeneration(generation);
            }
            catch (IllegalArgumentException ex)
            {
                throw new RuntimeException("Error serializing item! - Invalid Generation: '" + jsonObject.get("generation").getAsString() + "'", ex);
            }
        }

        if (jsonObject.has("pages"))
        {
            List<String> pages = new ArrayList<>();
            for (JsonObject jsonObj : jsonArrayToObjectList(jsonObject.get("pages").getAsJsonArray()))
            {
                pages.add(jsonObj.getAsString());
            }
            bookMeta.setPages(pages);
        }

        return bookMeta;
    }
    //endregion


    //region Firework
    /**
     * Serialize a {@link FireworkMeta} into a {@link JsonObject}
     */
    @NotNull
    private JsonObject serializeFirework(@NotNull FireworkMeta fireworkMeta)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("power", new JsonPrimitive(fireworkMeta.getPower()));

        if (fireworkMeta.hasEffects())
        {
            JsonArray effectArray = new JsonArray();
            for (FireworkEffect effect : fireworkMeta.getEffects())
            {
                JsonObject effectObject = new JsonObject();
                effectObject.add("type", new JsonPrimitive(effect.getType().toString()));
                effectObject.add("trail", new JsonPrimitive(effect.hasTrail()));
                effectObject.add("flicker", new JsonPrimitive(effect.hasFlicker()));

                JsonArray colorArray = new JsonArray();
                for (Color color : effect.getColors())
                {
                    colorArray.add(colorToJson(color));
                }
                effectObject.add("colors", colorArray);

                JsonArray fadeArray = new JsonArray();
                for (Color color : effect.getFadeColors())
                {
                    fadeArray.add(colorToJson(color));
                }
                effectObject.add("fadeColors", fadeArray);

                effectArray.add(effectObject);
            }

            jsonObject.add("effects", effectArray);
        }

        return jsonObject;
    }

    /**
     * Deserialize a {@link JsonObject} into a {@link FireworkMeta}
     */
    @NotNull
    private FireworkMeta deserializeFirework(@NotNull JsonObject jsonObject)
    {
        FireworkMeta fireworkMeta = (FireworkMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.FIREWORK_ROCKET);
        if (fireworkMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to FireworkMeta");

        fireworkMeta.setPower(jsonObject.get("power").getAsInt());

        if (jsonObject.has("effects"))
        {
            List<FireworkEffect> fireworkEffects = new ArrayList<>();
            for (JsonObject effectObject : jsonArrayToObjectList(jsonObject.get("effects").getAsJsonArray()))
            {
                try
                {
                    FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectObject.get("type").getAsString());

                    List<Color> effectColors = new ArrayList<>();
                    for (JsonObject jsonObj : jsonArrayToObjectList(effectObject.get("colors").getAsJsonArray()))
                    {
                        effectColors.add(jsonToColor(jsonObj));
                    }

                    List<Color> effectFades = new ArrayList<>();
                    for (JsonObject jsonObj : jsonArrayToObjectList(effectObject.get("fadeColors").getAsJsonArray()))
                    {
                        effectFades.add(jsonToColor(jsonObj));
                    }

                    FireworkEffect effect = FireworkEffect.builder().
                            with(effectType).
                            withFade(effectFades).
                            withColor(effectColors).
                            trail(effectObject.get("trail").getAsBoolean()).
                            flicker(effectObject.get("flicker").getAsBoolean()).
                            build();

                    fireworkEffects.add(effect);
                }
                catch (IllegalArgumentException ex)
                {
                    throw new RuntimeException("Error serializing item! - Invalid Type: '" + effectObject.get("type").getAsString() + "'", ex);
                }
            }

            fireworkMeta.addEffects(fireworkEffects);
        }

        return fireworkMeta;
    }
    //endregion


    //region Leather Armor
    /**
     * Serialize a {@link LeatherArmorMeta} into a {@link JsonObject}
     */
    @NotNull
    private JsonObject serializeLeatherArmor(@NotNull LeatherArmorMeta leatherArmorMeta)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("color", colorToJson(leatherArmorMeta.getColor()));
        return jsonObject;
    }

    /**
     * Deserialize a {@link JsonObject} into a {@link LeatherArmorMeta}
     */
    private LeatherArmorMeta deserializeLeatherArmor(JsonObject jsonObject)
    {
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.LEATHER_CHESTPLATE);
        if (leatherArmorMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to LeatherArmorMeta");

        Color armorColor = jsonToColor(jsonObject.get("color").getAsJsonObject());
        leatherArmorMeta.setColor(armorColor);

        return leatherArmorMeta;
    }
    //endregion


    //region Head/Skull
    /*
     * NOTE: Bukkit.getOfflinePlayer(UUID) seems to be bugged and will only find players who have joined the server.
     * Because of this, I am forced to use deprecated methods to get/set the owner of the skull.
     */

    /**
     * Serialize a {@link SkullMeta} into a {@link JsonObject}
     */
    @NotNull
    private JsonObject serializeSkull(@NotNull SkullMeta skullMeta)
    {
        JsonObject jsonObject = new JsonObject();

        if (skullMeta.hasOwner() && skullMeta.getOwner() != null)
        {
            jsonObject.add("owner", new JsonPrimitive(skullMeta.getOwner()));
        }

        return jsonObject;
    }

    /**
     * Deserialize a {@link JsonObject} into a {@link SkullMeta}
     */
    @NotNull
    private SkullMeta deserializeSkull(@NotNull JsonObject jsonObject)
    {
        SkullMeta skullMeta = (SkullMeta) pluginInstance.getServer().getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        if (skullMeta == null)
            throw new RuntimeException("Error serializing item! - Can't cast ItemMeta to SkullMeta");

        skullMeta.setOwner(jsonObject.get("owner").getAsString());

        return skullMeta;
    }
    //endregion
}
