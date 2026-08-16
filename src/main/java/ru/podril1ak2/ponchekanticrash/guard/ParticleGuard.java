package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.jetbrains.annotations.Nullable;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;

public final class ParticleGuard implements Guard {
    private final ConfigManager config;

    public ParticleGuard(ConfigManager config) {
        this.config = config;
    }

    @Override
    public String id() {
        return "particle";
    }

    @Override
    public @Nullable Threat inspect(Packet<?> packet) {
        if (!(packet instanceof ParticleS2CPacket particle)) {
            return null;
        }
        int limit = config.get().maxParticleCount;
        int count = particle.getCount();
        if (count >= 0 && count <= limit) {
            return null;
        }
        return new Threat(id(), "particle burst of " + count + ", limit is " + limit);
    }
}
