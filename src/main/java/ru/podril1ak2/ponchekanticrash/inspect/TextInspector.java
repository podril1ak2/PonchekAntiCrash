package ru.podril1ak2.ponchekanticrash.inspect;

import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public final class TextInspector {
    private TextInspector() {
    }

    public static @Nullable String findAnomaly(Text root, int maxNodes, int maxDepth, int maxLength) {
        Deque<Text> pending = new ArrayDeque<>();
        Deque<Integer> depths = new ArrayDeque<>();
        pending.add(root);
        depths.add(0);

        int visited = 0;
        long length = 0L;
        while (!pending.isEmpty()) {
            Text text = pending.poll();
            int depth = depths.poll();
            if (depth > maxDepth) {
                return "text nested deeper than " + maxDepth + " levels";
            }
            visited++;
            length += measure(text.getContent());
            if (length > maxLength) {
                return "text longer than " + maxLength + " characters";
            }
            for (Text sibling : text.getSiblings()) {
                if (visited + pending.size() >= maxNodes) {
                    return "text holds more than " + maxNodes + " components";
                }
                pending.add(sibling);
                depths.add(depth + 1);
            }
        }
        return null;
    }

    private static long measure(TextContent content) {
        long[] length = {0L};
        content.<Object>visit(literal -> {
            length[0] += literal.length();
            return Optional.empty();
        });
        return length[0];
    }
}
