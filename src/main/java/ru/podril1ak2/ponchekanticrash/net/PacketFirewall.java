package ru.podril1ak2.ponchekanticrash.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.packet.Packet;
import ru.podril1ak2.ponchekanticrash.guard.GuardPipeline;
import ru.podril1ak2.ponchekanticrash.guard.Threat;
import ru.podril1ak2.ponchekanticrash.report.ThreatReporter;

public final class PacketFirewall extends ChannelInboundHandlerAdapter {
    public static final String NAME = "ponchekanticrash_firewall";

    private final GuardPipeline pipeline;
    private final ThreatReporter reporter;

    public PacketFirewall(GuardPipeline pipeline, ThreatReporter reporter) {
        this.pipeline = pipeline;
        this.reporter = reporter;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof Packet<?> packet) {
            Threat threat = pipeline.inspect(packet);
            if (threat != null) {
                reporter.report(threat);
                ReferenceCountUtil.release(message);
                return;
            }
        }
        context.fireChannelRead(message);
    }
}
