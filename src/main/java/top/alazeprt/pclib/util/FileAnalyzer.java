package top.alazeprt.pclib.util;

import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileAnalyzer {
    public static String getNameByJar(String jarPath) throws IOException {
        try (JarFile jarFile = new JarFile(jarPath)) {
            // 遍历JAR条目
            JarEntry spigotPlugin = jarFile.getJarEntry("plugin.yml");
            JarEntry paperPlugin = jarFile.getJarEntry("paper-plugin.yml");
            JarEntry velocityPlugin = jarFile.getJarEntry("velocity-plugin.json");

            if (spigotPlugin == null && paperPlugin == null && velocityPlugin == null) {
                return "";
            }

            Map<String, Object> map;

            String platform = "";

            if (paperPlugin != null) {
                Yaml yaml = new Yaml();
                map = yaml.load(new InputStreamReader(jarFile.getInputStream(paperPlugin), StandardCharsets.UTF_8));
                platform = "Paper";
            } else if (spigotPlugin != null) {
                Yaml yaml = new Yaml();
                map = yaml.load(new InputStreamReader(jarFile.getInputStream(spigotPlugin), StandardCharsets.UTF_8));
                platform = "Spigot";
            } else {
                Gson gson = new Gson();
                map = gson.fromJson(new FileReader(jarPath), Map.class);
                platform = "Velocity";
            }

            if (map != null && !map.isEmpty() && map.containsKey("name")) {
                if (map.containsKey("version")) {
                    return map.get("name") + "-" + platform + "-" + map.get("version") + ".jar";
                } else {
                    return map.get("name") + "-" + platform + ".jar";
                }
            }
        }
        return "";
    }
}
