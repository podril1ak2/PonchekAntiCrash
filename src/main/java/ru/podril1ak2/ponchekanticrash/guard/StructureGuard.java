package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import ru.podril1ak2.ponchekanticrash.config.AntiCrashConfig;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.inspect.TextInspector;
import ru.podril1ak2.ponchekanticrash.inspect.ValueWalker;

public final class StructureGuard implements Guard {
    private final ConfigManager config;

    public StructureGuard(ConfigManager config) {
        this.config = config;
    }

    @Override
    public String id() {
        return "structure";
    }

    @Override
    public @Nullable Threat inspect(Packet<?> packet) {
        String anomaly = ValueWalker.walk(packet, new PayloadInspector(config.get()));
        if (anomaly == null) {
            return null;
        }
        return new Threat(id(), packet.getClass().getSimpleName() + ": " + anomaly);
    }

    private record PayloadInspector(AntiCrashConfig settings) implements ValueWalker.Inspector {
        @Override
        public @Nullable String number(double value) {
            return Double.isFinite(value) ? null : "non-finite value " + value;
        }

        @Override
        public @Nullable String text(Text text) {
            return TextInspector.findAnomaly(text, settings.maxTextNodes, settings.maxTextDepth, settings.maxTextLength);
        }
    }
}
