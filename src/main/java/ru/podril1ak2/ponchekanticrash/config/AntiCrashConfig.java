package ru.podril1ak2.ponchekanticrash.config;

import ru.podril1ak2.ponchekanticrash.guard.MonitoredPackets;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AntiCrashConfig {
    public boolean enabled = true;
    public boolean packetFirewall = true;
    public boolean dispatchSafetyNet = true;
    public boolean chatNotifications = true;
    public long notificationCooldownMillis = 4000L;

    public int maxParticleCount = 2048;
    public float maxSoundVolume = 24.0F;
    public float minSoundPitch = 0.0F;
    public float maxSoundPitch = 8.0F;

    public int maxTextNodes = 4096;
    public int maxTextDepth = 64;
    public int maxTextLength = 65536;

    public int rateWindowMillis = 1000;
    public Map<String, Integer> rateLimits = MonitoredPackets.defaultRateLimits();

    public AntiCrashConfig normalized() {
        notificationCooldownMillis = Math.max(0L, notificationCooldownMillis);
        maxParticleCount = Math.max(1, maxParticleCount);
        maxSoundVolume = Math.max(1.0F, maxSoundVolume);
        maxSoundPitch = Math.max(1.0F, maxSoundPitch);
        minSoundPitch = Math.max(0.0F, Math.min(minSoundPitch, maxSoundPitch));
        maxTextNodes = Math.max(64, maxTextNodes);
        maxTextDepth = Math.max(4, maxTextDepth);
        maxTextLength = Math.max(256, maxTextLength);
        rateWindowMillis = Math.max(50, rateWindowMillis);

        Map<String, Integer> merged = MonitoredPackets.defaultRateLimits();
        if (rateLimits != null) {
            rateLimits.forEach((id, limit) -> {
                if (limit != null && merged.containsKey(id)) {
                    merged.put(id, limit);
                }
            });
        }
        rateLimits = new LinkedHashMap<>(merged);
        return this;
    }
}
