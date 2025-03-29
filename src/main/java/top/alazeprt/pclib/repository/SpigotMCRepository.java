package top.alazeprt.pclib.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.alazeprt.pclib.util.Author;
import top.alazeprt.pclib.util.HttpUtil;
import top.alazeprt.pclib.util.Plugin;
import top.alazeprt.pclib.util.SpigotPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotMCRepository implements PluginRepository {
    @Override
    public List<Plugin> getPlugins(int size, int page) throws IOException {
        List<Plugin> list = new ArrayList<>();
        String data = HttpUtil.get("https://api.spiget.org/v2/resources", Map.of("Accept", "application/json"),
                Map.of("size", String.valueOf(size), "page", String.valueOf(page)));
        Gson gson = new Gson();
        try {
            JsonArray jsonArray = gson.fromJson(data, JsonArray.class);
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Plugin plugin = SpigotPlugin.fromJson(this, jsonObject);
                list.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Plugin> fastGetPlugins(int size, int page) throws IOException {
        List<Plugin> list = new ArrayList<>();
        String data = HttpUtil.get("https://api.spiget.org/v2/resources", Map.of("Accept", "application/json"),
                Map.of("size", String.valueOf(size), "page", String.valueOf(page)));
        Gson gson = new Gson();
        try {
            JsonArray jsonArray = gson.fromJson(data, JsonArray.class);
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Plugin plugin = SpigotPlugin.fastFromJson(this, jsonObject);
                list.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Plugin getPlugin(int pluginId) throws IOException {
        return getSpigotPlugin(pluginId);
    }

    public Author getAuthor(int authorId) throws IOException {
        String data = HttpUtil.get("https://api.spiget.org/v2/authors/" + authorId, Map.of("Accept", "application/json"), Map.of());
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
            return new Author(
                    this,
                    jsonObject.get("id").getAsInt(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.getAsJsonObject("icon").get("data").getAsString()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCategory(int categoryId) throws IOException {
        String data = HttpUtil.get("https://api.spiget.org/v2/categories/" + categoryId, Map.of("Accept", "application/json"), Map.of());
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
            return jsonObject.get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public SpigotPlugin getSpigotPlugin(int pluginId) throws IOException {
        String data = HttpUtil.get("https://api.spiget.org/v2/resources/" + pluginId, Map.of("Accept", "application/json"), Map.of());
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
            return SpigotPlugin.fromJson(this, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
