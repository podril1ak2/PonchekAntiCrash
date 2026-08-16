package ru.podril1ak2.ponchekanticrash.guard;

import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MonitoredPackets {
    private static final Map<Class<?>, Monitored> ENTRIES = index();

    private MonitoredPackets() {
    }

    public record Monitored(String id, int defaultLimit) {
    }

    public static @Nullable Monitored lookup(Class<?> type) {
        return ENTRIES.get(type);
    }

    public static Map<String, Integer> defaultRateLimits() {
        Map<String, Integer> limits = new LinkedHashMap<>();
        for (Monitored monitored : ENTRIES.values()) {
            limits.put(monitored.id(), monitored.defaultLimit());
        }
        return limits;
    }

    private static Map<Class<?>, Monitored> index() {
        Map<Class<?>, Monitored> entries = new LinkedHashMap<>();
        entries.put(ParticleS2CPacket.class, new Monitored("particle", 300));
        entries.put(PlaySoundS2CPacket.class, new Monitored("sound", 200));
        entries.put(PlaySoundFromEntityS2CPacket.class, new Monitored("sound_from_entity", 200));
        entries.put(ExplosionS2CPacket.class, new Monitored("explosion", 40));
        entries.put(WorldEventS2CPacket.class, new Monitored("world_event", 200));
        entries.put(EntitySpawnS2CPacket.class, new Monitored("entity_spawn", 600));
        entries.put(EntitiesDestroyS2CPacket.class, new Monitored("entity_destroy", 300));
        entries.put(EntityStatusS2CPacket.class, new Monitored("entity_status", 400));
        entries.put(EntityStatusEffectS2CPacket.class, new Monitored("entity_effect", 200));
        entries.put(EntityAttributesS2CPacket.class, new Monitored("entity_attributes", 200));
        entries.put(EntityPassengersSetS2CPacket.class, new Monitored("entity_passengers", 200));
        entries.put(GameMessageS2CPacket.class, new Monitored("chat", 120));
        entries.put(TitleS2CPacket.class, new Monitored("title", 40));
        entries.put(SubtitleS2CPacket.class, new Monitored("subtitle", 40));
        entries.put(OverlayMessageS2CPacket.class, new Monitored("action_bar", 60));
        entries.put(BossBarS2CPacket.class, new Monitored("boss_bar", 60));
        entries.put(TeamS2CPacket.class, new Monitored("team", 200));
        entries.put(ScoreboardObjectiveUpdateS2CPacket.class, new Monitored("scoreboard_objective", 100));
        entries.put(PlayerListS2CPacket.class, new Monitored("player_list", 100));
        entries.put(MapUpdateS2CPacket.class, new Monitored("map_update", 100));
        entries.put(OpenScreenS2CPacket.class, new Monitored("open_screen", 20));
        entries.put(InventoryS2CPacket.class, new Monitored("inventory", 100));
        entries.put(ScreenHandlerSlotUpdateS2CPacket.class, new Monitored("slot_update", 400));
        entries.put(CommandTreeS2CPacket.class, new Monitored("command_tree", 5));
        entries.put(AdvancementUpdateS2CPacket.class, new Monitored("advancements", 20));
        return Collections.unmodifiableMap(entries);
    }
}
