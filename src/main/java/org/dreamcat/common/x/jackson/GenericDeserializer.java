package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.ReflectUtil;

/**
 * Create by tuke on 2020/7/29
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class GenericDeserializer extends JsonDeserializer<Object>
        implements ContextualDeserializer {

    private Class<?> type;
    private Class<?> componentType;
    private ZoneId zoneId;

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (type.equals(List.class) || type.isArray()) {
            List list = p.readValueAs(List.class);
            List value = new ArrayList(list.size());
            for (Object e : list) {
                if (e == null) return null;
                value.add(map(e, componentType, zoneId));
            }
            if (type.isArray()) {
                return value.toArray((Object[]) Array.newInstance(componentType, value.size()));
            }
            return value;
        }

        return map(p.readValueAs(Object.class), type, zoneId);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        if (property == null) return this;

        GenericDeserialize genericDeserialize = property.getAnnotation(GenericDeserialize.class);
        if (genericDeserialize == null) {
            genericDeserialize = property.getContextAnnotation(GenericDeserialize.class);
        }

        if (genericDeserialize == null) {
            return ctxt.findNonContextualValueDeserializer(property.getType());
        }

        Class<?> rawClass = property.getType().getRawClass();
        Class<?> rawComponentType = genericDeserialize.componentType();
        ZoneId rawZoneId = ZoneId.of(genericDeserialize.zoneId());
        return new GenericDeserializer(rawClass, rawComponentType, rawZoneId);
    }

    private static Object map(Object value, Class<?> type, ZoneId zoneId) {
        try {
            if (value instanceof String) {
                String s = (String) value;
                return castStr(s, type);
            } else if (value instanceof Integer || value instanceof Long) {
                long n = ((Number) value).longValue();
                return castInt(n, type, zoneId);
            } else if (value instanceof Double) {
                double n = (Double) value;
                Object parsed = ReflectUtil.parse(n, type);
                if (parsed != null) return parsed;
            } else if (value instanceof Boolean) {
                Object parsed = ReflectUtil.parse((Boolean) value, type);
                if (parsed != null) return parsed;
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("failed to cast '{}' to {}: {}", value, type, e.getMessage());
            }
        }
        return null;
    }

    private static Object castStr(String s, Class<?> type) {
        // String to Numeric, Boolean
        Object parsed = ReflectUtil.parse(s, type);
        if (parsed != null) return parsed;
        // String to Enum
        if (type.isEnum()) {
            if (Supplier.class.isAssignableFrom(type)) {
                Object[] enums = type.getEnumConstants();
                for (Object e : enums) {
                    if (s.equals(((Supplier) e).get())) {
                        return e;
                    }
                }
            } else {
                return Enum.valueOf((Class<? extends Enum>) type, s);
            }
        }
        // String to Bean
        else return JacksonUtil.fromJson(s, type);
        return null;
    }

    private static Object castInt(long n, Class<?> type, ZoneId zoneId) {
        // Numeric to String, Date, LocalDate or LocalDateTime
        Object parsed = ReflectUtil.parse(n, type, zoneId);
        if (parsed != null) return parsed;

        // Numeric to Enum
        if (type.isEnum()) {
            Object[] enums = type.getEnumConstants();
            if (Supplier.class.isAssignableFrom(type)) {
                for (Object e : enums) {
                    Object v = ((Supplier) e).get();
                    if (v instanceof Number && ((Number) v).longValue() == n) {
                        return e;
                    }
                }
            } else if (IntSupplier.class.isAssignableFrom(type)) {
                for (Object e : enums) {
                    int v = ((IntSupplier) e).getAsInt();
                    if (v == n) {
                        return e;
                    }
                }
            } else {
                for (Object e : enums) {
                    int ordinal = ((Enum) e).ordinal();
                    if (ordinal == n) {
                        return e;
                    }
                }
            }
        }
        return null;
    }
}
