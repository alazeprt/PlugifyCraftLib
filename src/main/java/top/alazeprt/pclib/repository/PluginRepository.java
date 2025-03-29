package top.alazeprt.pclib.repository;

import top.alazeprt.pclib.util.Plugin;

import java.io.IOException;
import java.util.List;

public interface PluginRepository {
    List<Plugin> getPlugins(int size, int page) throws IOException;

    Plugin getPlugin(int pluginId) throws IOException;

    List<Plugin> search(String keyword, int size) throws IOException;
}
