package ru.podril1ak2.ponchekanticrash.inspect;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldIndex {
    private static final Field[] EMPTY = new Field[0];
    private static final Map<Class<?>, Field[]> CACHE = new ConcurrentHashMap<>();
    private static final Set<Class<?>> VECTORS =
            Set.of(Vec3d.class, Vec2f.class, Vector3f.class, Vector4f.class, Quaternionf.class);

    private FieldIndex() {
    }

    public static Field[] of(Class<?> type) {
        return CACHE.computeIfAbsent(type, FieldIndex::index);
    }

    public static boolean isTraversable(Class<?> type) {
        return type.isRecord() || VECTORS.contains(type);
    }

    private static Field[] index(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || !isRelevant(field.getType())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                } catch (RuntimeException exception) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields.isEmpty() ? EMPTY : fields.toArray(EMPTY);
    }

    private static boolean isRelevant(Class<?> type) {
        return type == double.class
                || type == float.class
                || Text.class.isAssignableFrom(type)
                || Optional.class.isAssignableFrom(type)
                || Collection.class.isAssignableFrom(type)
                || (type.isArray() && !type.getComponentType().isPrimitive())
                || isTraversable(type);
    }
}
