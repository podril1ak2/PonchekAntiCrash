package ru.podril1ak2.ponchekanticrash.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import ru.podril1ak2.ponchekanticrash.PonchekAntiCrash;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path path;
    private volatile AntiCrashConfig config = new AntiCrashConfig();

    public ConfigManager(Path path) {
        this.path = path;
    }

    public AntiCrashConfig get() {
        return config;
    }

    public void load() {
        if (Files.isRegularFile(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                AntiCrashConfig loaded = GSON.fromJson(reader, AntiCrashConfig.class);
                config = loaded == null ? new AntiCrashConfig() : loaded.normalized();
            } catch (IOException | JsonParseException exception) {
                PonchekAntiCrash.LOGGER.error("Failed to read {}, falling back to defaults", path, exception);
                config = new AntiCrashConfig();
            }
        }
        save();
    }

    public void save() {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temporary = path.resolveSibling(path.getFileName() + ".tmp");
            Files.writeString(temporary, GSON.toJson(config), StandardCharsets.UTF_8);
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            PonchekAntiCrash.LOGGER.error("Failed to write {}", path, exception);
        }
    }
}
