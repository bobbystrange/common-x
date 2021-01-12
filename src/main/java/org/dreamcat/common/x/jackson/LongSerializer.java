package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 * Create by tuke on 2020/7/29
 */
public class LongSerializer extends JsonSerializer<Long> {

    // @JsonSerialize(using = LongSerializer.class)
    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) gen.writeNull();
        else gen.writeString(Long.toString(value));
    }
}
