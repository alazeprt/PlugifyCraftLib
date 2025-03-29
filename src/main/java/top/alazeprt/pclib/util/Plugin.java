package top.alazeprt.pclib.util;

import top.alazeprt.pclib.repository.PluginRepository;

import java.util.Date;

public abstract class Plugin {
    public final PluginRepository repository;
    public final int id;
    public String name;
    public Author author;
    public int downloads;
    public String category;
    public Date releaseDate;
    public Date updateDate;
    public String description;
    public String image; // base64 encoded image

    public Plugin(PluginRepository repository, int id) {
        this.repository = repository;
        this.id = id;
    }

    public Plugin(PluginRepository repository, int id, String name, Author author, int downloads, String category, Date releaseDate, Date updateDate, String description) {
        this.repository = repository;
        this.id = id;
        this.name = name;
        this.author = author;
        this.downloads = downloads;
        this.category = category;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        this.description = description;
    }

    public Plugin(PluginRepository repository, int id, String name, Author author, int downloads, String category, Date releaseDate, Date updateDate, String description, String image) {
        this.repository = repository;
        this.id = id;
        this.name = name;
        this.author = author;
        this.downloads = downloads;
        this.category = category;
        this.releaseDate = releaseDate;
        this.updateDate = updateDate;
        this.description = description;
        this.image = image;
    }

}
