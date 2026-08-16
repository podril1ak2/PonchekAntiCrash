package ru.podril1ak2.ponchekanticrash.report;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.podril1ak2.ponchekanticrash.PonchekAntiCrash;
import ru.podril1ak2.ponchekanticrash.config.AntiCrashConfig;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.guard.Threat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ThreatReporter {
    private final ConfigManager config;
    private final Map<String, AtomicLong> perGuard = new ConcurrentHashMap<>();
    private final AtomicLong blocked = new AtomicLong();
    private final AtomicLong lastNotification = new AtomicLong();

    public ThreatReporter(ConfigManager config) {
        this.config = config;
    }

    public void report(Threat threat) {
        blocked.incrementAndGet();
        perGuard.computeIfAbsent(threat.guard(), key -> new AtomicLong()).incrementAndGet();
        PonchekAntiCrash.LOGGER.warn("Blocked [{}] {}", threat.guard(), threat.detail());
        announce(threat);
    }

    public long blocked() {
        return blocked.get();
    }

    public Map<String, Long> breakdown() {
        Map<String, Long> snapshot = new TreeMap<>();
        perGuard.forEach((guard, count) -> snapshot.put(guard, count.get()));
        return new LinkedHashMap<>(snapshot);
    }

    public void reset() {
        perGuard.clear();
        blocked.set(0L);
    }

    private void announce(Threat threat) {
        AntiCrashConfig settings = config.get();
        if (!settings.chatNotifications) {
            return;
        }
        long now = System.currentTimeMillis();
        long previous = lastNotification.get();
        if (now - previous < settings.notificationCooldownMillis || !lastNotification.compareAndSet(previous, now)) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        client.execute(() -> {
            if (client.player != null) {
                client.player.sendMessage(Text.translatable("message.ponchekanticrash.blocked", threat.detail())
                        .formatted(Formatting.RED), false);
            }
        });
    }
}
