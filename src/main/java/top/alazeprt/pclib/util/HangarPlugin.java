package top.alazeprt.pclib.util;

import com.google.gson.JsonObject;
import top.alazeprt.pclib.repository.HangarRepository;
import top.alazeprt.pclib.repository.PluginRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class HangarPlugin extends Plugin {
    public HangarPlugin(PluginRepository repository, int id) {
        super(repository, id);
    }

    public HangarPlugin(PluginRepository repository, int id, String name, Author author, int downloads, String category, Date releaseDate, Date updateDate, String description, String image) {
        super(repository, id, name, author, downloads, category, releaseDate, updateDate, description, image);
    }

    public static HangarPlugin fromJson(HangarRepository repository, JsonObject jsonObject) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        return new HangarPlugin(repository,
                jsonObject.get("id").getAsInt(),
                jsonObject.get("name").getAsString(),
                repository.getAuthor(jsonObject.getAsJsonObject("namespace").get("owner").getAsString()),
                jsonObject.getAsJsonObject("stats").get("downloads").getAsInt(),
                jsonObject.get("category").getAsString(),
                Date.from(Instant.from(formatter.parse(jsonObject.get("createdAt").getAsString()))),
                Date.from(Instant.from(formatter.parse(jsonObject.get("lastUpdated").getAsString()))),
                jsonObject.get("description").getAsString(),
                HttpUtil.getImageEncoded(jsonObject.get("avatarUrl").getAsString()));
    }
}
