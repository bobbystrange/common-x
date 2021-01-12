package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 * Create by tuke on 2020/7/29
 */
public class LongDeserializer extends JsonDeserializer<Long> {

    // @JsonDeserialize(using = LongDeserializer.class)
    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return Long.parseLong(p.getValueAsString());
        } catch (NumberFormatException e) {
            // Note that also catch `new NumberFormatException("null")`
            return null;
        }
    }
}
