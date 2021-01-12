package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.List;

/**
 * Create by tuke on 2020/7/29
 *
 * @see JacksonAnnotationsInside
 */
public class LongListSerializer extends JsonSerializer<List<Long>> {

    @Override
    public void serialize(List<Long> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // // or
        // String[] strings = value.stream().map(String::valueOf).toArray(String[]::new);
        // gen.writeArray(strings, 0, strings.length);
        gen.writeStartArray();
        for (Long n : value) {
            gen.writeString(n.toString());
        }
        gen.writeEndArray();
    }
}
