package ru.podril1ak2.ponchekanticrash.command;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.podril1ak2.ponchekanticrash.config.ConfigManager;
import ru.podril1ak2.ponchekanticrash.report.ThreatReporter;

public final class AntiCrashCommand {
    private AntiCrashCommand() {
    }

    public static void register(ConfigManager config, ThreatReporter reporter) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(
                ClientCommandManager.literal("anticrash")
                        .executes(context -> status(context.getSource(), config, reporter))
                        .then(ClientCommandManager.literal("on")
                                .executes(context -> toggle(context.getSource(), config, true)))
                        .then(ClientCommandManager.literal("off")
                                .executes(context -> toggle(context.getSource(), config, false)))
                        .then(ClientCommandManager.literal("reload")
                                .executes(context -> reload(context.getSource(), config)))
                        .then(ClientCommandManager.literal("reset")
                                .executes(context -> reset(context.getSource(), reporter)))));
    }

    private static int status(FabricClientCommandSource source, ConfigManager config, ThreatReporter reporter) {
        source.sendFeedback(Text.translatable("command.ponchekanticrash.status", state(config), reporter.blocked())
                .formatted(Formatting.GOLD));
        reporter.breakdown().forEach((guard, count) -> source.sendFeedback(
                Text.translatable("command.ponchekanticrash.breakdown", guard, count).formatted(Formatting.GRAY)));
        return Command.SINGLE_SUCCESS;
    }

    private static int toggle(FabricClientCommandSource source, ConfigManager config, boolean enabled) {
        config.get().enabled = enabled;
        config.save();
        source.sendFeedback(Text.translatable("command.ponchekanticrash.toggled", state(config)));
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(FabricClientCommandSource source, ConfigManager config) {
        config.load();
        source.sendFeedback(Text.translatable("command.ponchekanticrash.reloaded").formatted(Formatting.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int reset(FabricClientCommandSource source, ThreatReporter reporter) {
        reporter.reset();
        source.sendFeedback(Text.translatable("command.ponchekanticrash.reset").formatted(Formatting.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static Text state(ConfigManager config) {
        boolean enabled = config.get().enabled;
        return Text.translatable(enabled ? "command.ponchekanticrash.on" : "command.ponchekanticrash.off")
                .formatted(enabled ? Formatting.GREEN : Formatting.RED);
    }
}
