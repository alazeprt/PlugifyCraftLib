package top.alazeprt.pclib.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.alazeprt.pclib.util.Author;
import top.alazeprt.pclib.util.HangarPlugin;
import top.alazeprt.pclib.util.HttpUtil;
import top.alazeprt.pclib.util.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class HangarRepository implements PluginRepository {
    @Override
    public List<Plugin> getPlugins(int size, int page) throws IOException {
        List<Plugin> list = new ArrayList<>();
        Gson gson = new Gson();
        String data = HttpUtil.get("https://hangar.papermc.io/api/v1/projects", Map.of("Accept", "application/json"), Map.of(
                "pagination", gson.toJson(Map.of("offset", (page-1)*size, "limit", size))
        ));
        try {
            JsonArray jsonArray = gson.fromJson(data, JsonObject.class).get("result").getAsJsonArray();
            for (int i = 0; i < size; i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                Plugin plugin = HangarPlugin.fromJson(this, jsonObject);
                list.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Plugin getPlugin(int pluginId) throws IOException {
        return getHangarPlugin(pluginId);
    }

    @Override
    public List<Plugin> search(String keyword, int size) throws IOException {
        List<Plugin> list = new ArrayList<>();
        Gson gson = new Gson();
        String data = HttpUtil.get("https://hangar.papermc.io/api/v1/projects", Map.of("Accept", "application/json"),
                Map.of("offset", String.valueOf(0), "limit", String.valueOf(size), "query", keyword));
        try {
            JsonArray jsonArray = gson.fromJson(data, JsonObject.class).get("result").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                Plugin plugin = HangarPlugin.fromJson(this, jsonObject);
                list.add(plugin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Author getAuthor(String name) throws IOException {
        String data = HttpUtil.get("https://hangar.papermc.io/api/v1/users/" + name, Map.of("Accept", "application/json"), Map.of());
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
            return new Author(
                    this,
                    jsonObject.get("id").getAsInt(),
                    jsonObject.get("name").getAsString(),
                    HttpUtil.getImageEncoded(jsonObject.get("avatarUrl").getAsString())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HangarPlugin getHangarPlugin(int pluginId) throws IOException {
        Gson gson = new Gson();
        String data = HttpUtil.get("https://hangar.papermc.io/api/v1/projects/" + pluginId, Map.of("Accept", "application/json"), Map.of());
        try {
            JsonObject jsonObject = gson.fromJson(data, JsonObject.class);
            return HangarPlugin.fromJson(this, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
