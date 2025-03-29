package top.alazeprt.pclib.util;

import com.google.gson.JsonObject;
import top.alazeprt.pclib.repository.PluginRepository;
import top.alazeprt.pclib.repository.SpigotMCRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;

public class SpigotPlugin extends Plugin {

    public int rating;

    public SpigotPlugin(PluginRepository repository, int id) {
        super(repository, id);
    }

    public SpigotPlugin(PluginRepository repository, int id, String name, Author author, int downloads, String category, long releaseDate, long updateDate, int rating, String description, String image) {
        super(repository, id, name, author, downloads, category, new Date(releaseDate), new Date(updateDate), description, image);
        this.rating = rating;
    }

    public static SpigotPlugin fromJson(SpigotMCRepository repository, JsonObject jsonObject) throws IOException {
        return new SpigotPlugin(repository,
                jsonObject.get("id").getAsInt(),
                jsonObject.get("name").getAsString(),
                repository.getAuthor(jsonObject.getAsJsonObject("author").get("id").getAsInt()),
                jsonObject.get("downloads").getAsInt(),
                repository.getCategory(jsonObject.getAsJsonObject("category").get("id").getAsInt()),
                jsonObject.get("releaseDate").getAsLong(),
                jsonObject.get("updateDate").getAsLong(),
                jsonObject.getAsJsonObject("rating").get("average").getAsInt(),
                jsonObject.get("description").isJsonNull() ? "" : new String(Base64.getDecoder().decode(jsonObject.get("description").getAsString())),
                jsonObject.getAsJsonObject("icon").get("data").getAsString());
    }
}
