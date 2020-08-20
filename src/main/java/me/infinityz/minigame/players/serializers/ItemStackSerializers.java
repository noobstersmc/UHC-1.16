package me.infinityz.minigame.players.serializers;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class ItemStackSerializers {
    
    private ItemStackSerializers() {
      throw new IllegalStateException("Utility class");
    }

    public static JsonSerializer<ItemStack[]> getItemStackSerializerNoItems() {
        return (src, typeOfSrc, context) -> {
            var json = new JsonObject();
            json.addProperty("items",
                    Arrays.stream(src).filter(it -> it != null && it.getType() != Material.AIR).count());
            return json;
        };
    }

    public static JsonSerializer<ItemStack[]> getItemStackArraySerializer() {
        return (src, typeOfSrc, context) -> {
            var jsonItemStackArray = new JsonArray();

            var itemStackSerializer = getItemStackSerializer();
            int count = -1;
            for (var itemStack : src) {
                count++;
                if (itemStack == null)
                    continue;

                var jsonElement = new JsonObject();
                var itemStackJson = itemStackSerializer.serialize(itemStack, typeOfSrc, context);
                jsonElement.addProperty("position", count);
                jsonElement.add("item", itemStackJson);
                jsonItemStackArray.add(jsonElement);

            }
            return jsonItemStackArray;
        };
    }

    @SuppressWarnings("all")
    public static JsonSerializer<ItemStack> getItemStackSerializer(){
        return (src, typeOfSrc, context)->{
            var itemStackJson = new JsonObject();
    
            if (src != null) {
                itemStackJson.addProperty("type", src.getType().toString());
                itemStackJson.addProperty("amount", src.getAmount());
                if (src.getType() != Material.ENCHANTED_BOOK) {
                    var metaJson = new Gson().toJsonTree(src.getItemMeta()).getAsJsonObject();
                    // Remove all the not necessary data
                    metaJson.remove("persistentDataContainer");
                    metaJson.remove("unhandledTags");
                    metaJson.remove("destroyableKeys");
                    metaJson.remove("placeableKeys");
                    metaJson.remove("hideFlag");
                    metaJson.remove("unbreakable");
                    metaJson.remove("damage");
                    metaJson.remove("version");
                    metaJson.remove("repairCost");
                    if (metaJson.has("attributeModifiers"))
                        metaJson.remove("attributeModifiers");
                    if (metaJson.has("enchantments")) {
                        metaJson.remove("enchantments");
                        var enchantMetaJson = new JsonObject();
                        for (var enchantMetaEntry : src.getItemMeta().getEnchants().entrySet())
                            enchantMetaJson.addProperty(enchantMetaEntry.getKey().getKey().getKey(),
                                    enchantMetaEntry.getValue());
    
                        metaJson.add("enchantments", enchantMetaJson);
                    }
                    itemStackJson.add("meta", metaJson);
                } else {
                    var metaJson = new JsonObject();
                    var bookMeta = (EnchantmentStorageMeta) src.getItemMeta();
                    for (var storedEnchantEntry : bookMeta.getStoredEnchants().entrySet())
                        metaJson.addProperty(storedEnchantEntry.getKey().getKey().getKey(), storedEnchantEntry.getValue());
    
                    itemStackJson.add("meta", metaJson);
    
                }
    
                if (itemStackJson.has("meta") && itemStackJson.getAsJsonObject("meta").entrySet().isEmpty())
                    itemStackJson.remove("meta");
    
            }

            return itemStackJson;
    

        };
    }

}