package ru.podril1ak2.ponchekanticrash.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import ru.podril1ak2.ponchekanticrash.PonchekAntiCrash;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.guard.Threat;
import ru.podril1ak2.ponchekanticrash.report.ThreatReporter;
import ru.podril1ak2.ponchekanticrash.util.Rethrow;

@Mixin(ClientConnection.class)
public abstract class PacketDispatchMixin {
    @WrapMethod(method = "handlePacket")
    private static void ponchekanticrash$catchDispatchFailure(Packet<?> packet, PacketListener listener, Operation<Void> original) {
        try {
            original.call(packet, listener);
        } catch (Throwable throwable) {
            ConfigManager config = PonchekAntiCrash.config();
            ThreatReporter reporter = PonchekAntiCrash.reporter();
            if (config == null || reporter == null
                    || !config.get().enabled
                    || !config.get().dispatchSafetyNet
                    || throwable instanceof OutOfMemoryError) {
                Rethrow.unchecked(throwable);
                return;
            }
            reporter.report(new Threat("dispatch", packet.getClass().getSimpleName() + " threw " + throwable));
        }
    }
}
