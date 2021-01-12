package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/29
 *
 * @see JacksonAnnotationsInside
 */
@SuppressWarnings("unchecked")
public class LongListDeserializer extends JsonDeserializer<List<Long>> {

    @Override
    public List<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ((List<String>) p.readValueAs(List.class)).stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

}
