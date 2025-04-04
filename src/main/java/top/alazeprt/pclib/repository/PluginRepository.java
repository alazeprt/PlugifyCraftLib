package top.alazeprt.pclib.repository;

import com.google.gson.JsonArray;
import top.alazeprt.pclib.util.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PluginRepository {
    List<Plugin> getPlugins(int size, int page) throws IOException;

    Plugin getPlugin(int pluginId) throws IOException;

    List<Plugin> search(String keyword, int size) throws IOException;

    Map<String, Integer> getVersions(int pluginId) throws IOException;

    File download(int pluginId, int versionId, int threadCount, File path) throws IOException;
}
