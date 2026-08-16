package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;
import ru.podril1ak2.ponchekanticrash.PonchekAntiCrash;
import ru.podril1ak2.ponchekanticrash.config.AntiCrashConfig;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;

import java.util.List;

public final class GuardPipeline {
    private final ConfigManager config;
    private final List<Guard> guards;

    public GuardPipeline(ConfigManager config) {
        this.config = config;
        this.guards = List.of(
                new RateLimitGuard(config),
                new ParticleGuard(config),
                new SoundGuard(config),
                new StructureGuard(config)
        );
    }

    public @Nullable Threat inspect(Packet<?> packet) {
        AntiCrashConfig settings = config.get();
        if (!settings.enabled || !settings.packetFirewall) {
            return null;
        }
        for (Guard guard : guards) {
            try {
                Threat threat = guard.inspect(packet);
                if (threat != null) {
                    return threat;
                }
            } catch (Throwable throwable) {
                PonchekAntiCrash.LOGGER.debug("Guard {} failed on {}", guard.id(), packet.getClass().getName(), throwable);
            }
        }
        return null;
    }
}
