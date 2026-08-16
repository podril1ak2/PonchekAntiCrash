package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import ru.podril1ak2.ponchekanticrash.config.AntiCrashConfig;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.util.RateWindow;

import java.util.HashMap;
import java.util.Map;

public final class RateLimitGuard implements Guard {
    private final ConfigManager config;
    private final Map<Class<?>, RateWindow> windows = new HashMap<>();

    public RateLimitGuard(ConfigManager config) {
        this.config = config;
    }

    @Override
    public String id() {
        return "rate";
    }

    @Override
    public @Nullable Threat inspect(Packet<?> packet) {
        MonitoredPackets.Monitored monitored = MonitoredPackets.lookup(packet.getClass());
        if (monitored == null) {
            return null;
        }
        AntiCrashConfig settings = config.get();
        Integer limit = settings.rateLimits.get(monitored.id());
        if (limit == null || limit <= 0) {
            return null;
        }
        RateWindow window = windows.computeIfAbsent(packet.getClass(), type -> new RateWindow());
        if (!window.exceeds(limit, settings.rateWindowMillis * 1_000_000L)) {
            return null;
        }
        return new Threat(id(), monitored.id() + " flood, over " + limit + " packets per " + settings.rateWindowMillis + "ms");
    }
}
