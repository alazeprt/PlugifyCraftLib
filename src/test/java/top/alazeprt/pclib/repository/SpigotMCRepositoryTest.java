package top.alazeprt.pclib.repository;

import org.junit.jupiter.api.Test;
import top.alazeprt.pclib.util.Plugin;
import top.alazeprt.pclib.util.SpigotPlugin;

import java.io.IOException;
import java.util.List;

public class SpigotMCRepositoryTest {
    @Test
    public void getPlugins() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        List<Plugin> plugins = repository.fastGetPlugins(20, 114);
        for (Plugin plugin : plugins) {
            System.out.println(plugin.name);
        }
    }

    @Test
    public void getPlugin() throws IOException {
        SpigotMCRepository repository = new SpigotMCRepository();
        SpigotPlugin plugin = repository.getSpigotPlugin(83767);
        System.out.println(plugin.author.name);
    }
}
