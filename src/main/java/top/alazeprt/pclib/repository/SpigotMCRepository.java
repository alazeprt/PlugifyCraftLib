package top.alazeprt.pclib.repository;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.alazeprt.pclib.util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

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

    @Override
    public List<Plugin> search(String keyword, int size) throws IOException {
        List<Plugin> list = new ArrayList<>();
        String data = HttpUtil.get("https://api.spiget.org/v2/search/resources/" + keyword, Map.of("Accept", "application/json"), Map.of("size", String.valueOf(size)));
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

    public List<Plugin> fastSearch(String keyword, int size) throws IOException {
        List<Plugin> list = new ArrayList<>();
        String data = HttpUtil.get("https://api.spiget.org/v2/search/resources/" + keyword, Map.of("Accept", "application/json"), Map.of("size", String.valueOf(size)));
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

    @Override
    public Map<String, Integer> getVersions(int pluginId) throws IOException {
        String data = HttpUtil.get("https://api.spiget.org/v2/resources/" + pluginId + "/versions", Map.of("Accept", "application/json"), Map.of("size", String.valueOf(10000000)));
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(data, JsonArray.class);
        jsonArray = JsonUtil.sortByReleaseDate(jsonArray);
        Map<String, Integer> versions = new LinkedHashMap<>();
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            versions.put(jsonObject.get("name").getAsString(), jsonObject.get("id").getAsInt());
        }
        return versions;
    }

    @Override
    public File download(int pluginId, int versionId, int threadCount, File path) throws IOException {
        String url = "https://api.spiget.org/v2/resources/" + pluginId + "/versions/" + versionId + "/download/proxy";
        return MultiThreadDownloader.download(url, threadCount, path);
    }

    @Override
    public File download(int pluginId, int versionId, int threadCount, File path, Consumer<Long> consumer) throws IOException {
        String url = "https://api.spiget.org/v2/resources/" + pluginId + "/versions/" + versionId + "/download/proxy";
        return MultiThreadDownloader.download(url, threadCount, path, consumer);
    }
}
