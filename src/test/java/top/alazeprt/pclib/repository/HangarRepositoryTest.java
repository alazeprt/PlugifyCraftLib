package top.alazeprt.pclib.repository;

import org.junit.jupiter.api.Test;
import top.alazeprt.pclib.util.Plugin;

import java.io.IOException;
import java.util.List;

public class HangarRepositoryTest {
    @Test
    public void getPlugins() throws IOException {
        HangarRepository repository = new HangarRepository();
        List<Plugin> plugin = repository.getPlugins(10, 1);
        plugin.forEach(plugin2 -> System.out.println(plugin2.description));
    }

    @Test
    public void getPlugin() throws IOException {
        HangarRepository repository = new HangarRepository();
        Plugin plugin = repository.getPlugin(5);
        System.out.println(plugin.author.image);
    }

}