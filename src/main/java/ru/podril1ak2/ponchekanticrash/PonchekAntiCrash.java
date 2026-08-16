package ru.podril1ak2.ponchekanticrash;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.podril1ak2.ponchekanticrash.command.AntiCrashCommand;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.report.ThreatReporter;

public final class PonchekAntiCrash implements ClientModInitializer {
    public static final String MOD_ID = "ponchekanticrash";
    public static final Logger LOGGER = LoggerFactory.getLogger("PonchekAntiCrash");

    private static ConfigManager config;
    private static ThreatReporter reporter;

    @Override
    public void onInitializeClient() {
        config = new ConfigManager(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json"));
        config.load();
        reporter = new ThreatReporter(config);
        AntiCrashCommand.register(config, reporter);
        LOGGER.info("PonchekAntiCrash initialised, packet firewall {}", config.get().enabled ? "enabled" : "disabled");
    }

    public static @Nullable ConfigManager config() {
        return config;
    }

    public static @Nullable ThreatReporter reporter() {
        return reporter;
    }
}
