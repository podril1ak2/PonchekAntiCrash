package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

public interface Guard {
    String id();

    @Nullable
    Threat inspect(Packet<?> packet);
}
