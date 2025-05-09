package top.alazeprt.pclib.repository;

import org.junit.jupiter.api.Test;
import top.alazeprt.pclib.util.Plugin;
import top.alazeprt.pclib.util.SpigotPlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class SpigotMCRepositoryTest {
    @Test
    public void getPlugins() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        List<Plugin> plugins = repository.fastGetPlugins(20, Math.abs(new Random().nextInt()%200)+1);
        for (Plugin plugin : plugins) {
            System.out.println(plugin.updateDate);
        }
    }

    @Test
    public void getPlugin() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        SpigotPlugin plugin = repository.getSpigotPlugin(83767);
        System.out.println(plugin.author.name);
    }

    @Test
    public void search() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        List<Plugin> plugins = repository.fastSearch("Husk", 20);
        for (Plugin plugin : plugins) {
            System.out.println(plugin.name);
        }
    }

    @Test
    public void getVersions() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        Map<String, Integer> versions = repository.getVersions(83767);
        for (Map.Entry<String, Integer> entry : versions.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @Test
    public void download() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        repository.download(83767, 581034, 4, new File("./"), System.out::println);
    }
}
