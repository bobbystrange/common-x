package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.TimeUtil;

/**
 * Create by tuke on 2020/7/30
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class GenericSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private ZoneId zoneId;
    private boolean longCast;
    private boolean booleanCast;

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            gen.writeStartArray();
            for (Object i : collection) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        }
        // primitive array
        else if (value instanceof long[]) {
            long[] array = (long[]) value;
            gen.writeStartArray();
            for (long i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof int[]) {
            int[] array = (int[]) value;
            gen.writeStartArray();
            for (int i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof double[]) {
            double[] array = (double[]) value;
            gen.writeStartArray();
            for (double i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof short[]) {
            short[] array = (short[]) value;
            gen.writeStartArray();
            for (short i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof byte[]) {
            byte[] array = (byte[]) value;
            gen.writeStartArray();
            for (byte i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof float[]) {
            float[] array = (float[]) value;
            gen.writeStartArray();
            for (float i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof char[]) {
            char[] array = (char[]) value;
            gen.writeStartArray();
            for (char i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        } else if (value instanceof boolean[]) {
            boolean[] array = (boolean[]) value;
            gen.writeStartArray();
            for (boolean i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        }
        // object array
        else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            gen.writeStartArray();
            for (Object i : array) {
                mapAndWrite(gen, i);
            }
            gen.writeEndArray();
        }
        // simple object
        else {
            mapAndWrite(gen, value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {
        if (property == null) return this;

        GenericSerialize genericSerialize = property.getAnnotation(GenericSerialize.class);
        if (genericSerialize == null) {
            genericSerialize = property.getContextAnnotation(GenericSerialize.class);
        }

        if (genericSerialize == null) {
            return prov.findContentValueSerializer(property.getType(), property);
        }

        ZoneId rawZoneId = ZoneId.of(genericSerialize.zoneId());
        boolean rawLongCast = genericSerialize.longCast();
        boolean rawBooleanCast = genericSerialize.booleanCast();
        return new GenericSerializer(rawZoneId, rawLongCast, rawBooleanCast);
    }

    private void mapAndWrite(JsonGenerator gen, Object value) throws IOException {
        if (value == null) {
            gen.writeNull();
        }
        // Boolean to 1/0
        else if (value instanceof Boolean) {
            if (booleanCast) gen.writeNumber((boolean) value ? 1 : 0);
            else gen.writeBoolean((boolean) value);
        }
        // Long to String
        else if (value instanceof Long) {
            if (longCast) gen.writeString(Long.toString((Long) value));
            else gen.writeNumber((Long) value);
        } else if (value instanceof Number) {
            gen.writeNumber(((Number) value).longValue());
        }
        // Date, LocalDate, LocalDateTime or Enum to Numeric
        else if (value instanceof Date) {
            gen.writeNumber(((Date) value).getTime());
        } else if (value instanceof LocalDateTime) {
            long epochMilli = TimeUtil.asEpochMilli((LocalDateTime) value, zoneId);
            gen.writeNumber(epochMilli);
        } else if (value instanceof LocalDate) {
            long epochMilli = TimeUtil.asEpochMilli(((LocalDate) value).atStartOfDay(), zoneId);
            gen.writeNumber(epochMilli);
        } else if (value instanceof Supplier) {
            Object enumValue = ((Supplier<?>) value).get();
            if (enumValue instanceof Number) {
                gen.writeNumber(((Number) enumValue).longValue());
            } else if (enumValue instanceof String) {
                gen.writeString((String) enumValue);
            } else {
                gen.writeNull();
            }
        } else if (value instanceof IntSupplier) {
            int n = ((IntSupplier) value).getAsInt();
            gen.writeNumber(n);
        } else if (value instanceof Enum) {
            String enumValue = ((Enum<?>) value).name();
            gen.writeString(enumValue);
        } else {
            gen.writeString(value.toString());
        }
    }
}
