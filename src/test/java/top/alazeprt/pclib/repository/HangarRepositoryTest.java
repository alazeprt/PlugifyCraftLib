package top.alazeprt.pclib.repository;

import org.junit.jupiter.api.Test;
import top.alazeprt.pclib.util.Plugin;

import java.io.File;
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
        System.out.println(plugin.image);
    }

    @Test
    public void search() throws IOException {
        HangarRepository repository = new HangarRepository();
        List<Plugin> plugins = repository.search("husk", 10);
        for (Plugin plugin : plugins) {
            System.out.println(plugin.name);
        }
    }

    @Test
    public void download() throws IOException {
        HangarRepository repository = new HangarRepository();
        repository.download(463, repository.getVersions(463).entrySet().stream().findFirst().get().getValue(), 4, new File("./"));
    }

}