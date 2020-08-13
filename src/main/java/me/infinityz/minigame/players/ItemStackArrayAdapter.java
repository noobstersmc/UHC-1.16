package me.infinityz.minigame.players;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.bukkit.inventory.ItemStack;

public class ItemStackArrayAdapter implements JsonSerializer<ItemStack[]> {

    @Override
    public JsonElement serialize(ItemStack[] src, Type srcType, JsonSerializationContext context) {
        var jsonItemStackArray = new JsonArray();

        var itemStackSerializer = new ItemStackAdapter();
        int count = -1;
        for (var itemStack : src) {
            count++;
            if (itemStack == null)
                continue;

            var jsonElement = new JsonObject();
            var itemStackJson = itemStackSerializer.serialize(itemStack, srcType, context);
            jsonElement.addProperty("position", count);
            jsonElement.add("item", itemStackJson);
            jsonItemStackArray.add(jsonElement);

        }
        return jsonItemStackArray;
    }

}