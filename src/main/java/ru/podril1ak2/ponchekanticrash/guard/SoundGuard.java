package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.jetbrains.annotations.Nullable;
import ru.podril1ak2.ponchekanticrash.config.AntiCrashConfig;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;

public final class SoundGuard implements Guard {
    private final ConfigManager config;

    public SoundGuard(ConfigManager config) {
        this.config = config;
    }

    @Override
    public String id() {
        return "sound";
    }

    @Override
    public @Nullable Threat inspect(Packet<?> packet) {
        if (packet instanceof PlaySoundS2CPacket sound) {
            return validate(sound.getVolume(), sound.getPitch());
        }
        if (packet instanceof PlaySoundFromEntityS2CPacket sound) {
            return validate(sound.getVolume(), sound.getPitch());
        }
        return null;
    }

    private @Nullable Threat validate(float volume, float pitch) {
        AntiCrashConfig settings = config.get();
        if (volume < 0.0F || volume > settings.maxSoundVolume) {
            return new Threat(id(), "sound volume " + volume + " outside 0.0.." + settings.maxSoundVolume);
        }
        if (pitch < settings.minSoundPitch || pitch > settings.maxSoundPitch) {
            return new Threat(id(), "sound pitch " + pitch + " outside " + settings.minSoundPitch + ".." + settings.maxSoundPitch);
        }
        return null;
    }
}
