package top.alazeprt.pclib.util;

import top.alazeprt.pclib.repository.PluginRepository;

public class Author {
    private final PluginRepository repository;
    private final int id;
    public String name;
    public String image; // base64 encoded image

    public Author(PluginRepository repository, int id, String name, String image) {
        this.repository = repository;
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
