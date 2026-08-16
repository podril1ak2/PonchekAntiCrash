package ru.podril1ak2.ponchekanticrash.mixin.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.podril1ak2.ponchekanticrash.PonchekAntiCrash;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.guard.GuardPipeline;
import ru.podril1ak2.ponchekanticrash.net.PacketFirewall;
import ru.podril1ak2.ponchekanticrash.report.ThreatReporter;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Inject(method = "channelActive", at = @At("TAIL"))
    private void ponchekanticrash$installFirewall(ChannelHandlerContext context, CallbackInfo info) {
        ConfigManager config = PonchekAntiCrash.config();
        ThreatReporter reporter = PonchekAntiCrash.reporter();
        if (config == null || reporter == null || !(context.channel() instanceof SocketChannel)) {
            return;
        }
        ChannelPipeline pipeline = context.pipeline();
        if (pipeline.get(PacketFirewall.NAME) != null) {
            return;
        }
        try {
            pipeline.addBefore(context.name(), PacketFirewall.NAME, new PacketFirewall(new GuardPipeline(config), reporter));
            PonchekAntiCrash.LOGGER.info("Packet firewall installed on {}", context.channel().remoteAddress());
        } catch (RuntimeException exception) {
            PonchekAntiCrash.LOGGER.error("Could not install the packet firewall", exception);
        }
    }
}
