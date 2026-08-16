package ru.podril1ak2.ponchekanticrash.inspect;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class ValueWalker {
    private static final int MAX_DEPTH = 4;
    private static final int MAX_ELEMENTS = 128;

    private ValueWalker() {
    }

    public interface Inspector {
        @Nullable
        String number(double value);

        @Nullable
        String text(Text text);
    }

    public static @Nullable String walk(Object root, Inspector inspector) {
        return walkFields(root, inspector, 0);
    }

    private static @Nullable String walkFields(Object owner, Inspector inspector, int depth) {
        for (Field field : FieldIndex.of(owner.getClass())) {
            Object value;
            try {
                value = field.get(owner);
            } catch (ReflectiveOperationException | RuntimeException exception) {
                continue;
            }
            String anomaly = walkValue(value, field.getType(), inspector, depth);
            if (anomaly != null) {
                return anomaly;
            }
        }
        return null;
    }

    private static @Nullable String walkValue(@Nullable Object value, Class<?> declared, Inspector inspector, int depth) {
        if (declared == double.class || declared == float.class) {
            return inspector.number(((Number) value).doubleValue());
        }
        if (value == null) {
            return null;
        }
        if (value instanceof Text text) {
            return inspector.text(text);
        }
        if (value instanceof Double || value instanceof Float) {
            return inspector.number(((Number) value).doubleValue());
        }
        if (depth >= MAX_DEPTH) {
            return null;
        }
        if (value instanceof Optional<?> optional) {
            return optional.isEmpty() ? null : walkValue(optional.get(), optional.get().getClass(), inspector, depth + 1);
        }
        if (value instanceof Collection<?> collection) {
            return walkElements(collection, inspector, depth + 1);
        }
        if (value instanceof Object[] array) {
            return walkElements(Arrays.asList(array), inspector, depth + 1);
        }
        return FieldIndex.isTraversable(value.getClass()) ? walkFields(value, inspector, depth + 1) : null;
    }

    private static @Nullable String walkElements(Iterable<?> elements, Inspector inspector, int depth) {
        int budget = MAX_ELEMENTS;
        for (Object element : elements) {
            if (budget-- <= 0) {
                return null;
            }
            String anomaly = walkValue(element, element == null ? Object.class : element.getClass(), inspector, depth);
            if (anomaly != null) {
                return anomaly;
            }
        }
        return null;
    }
}
