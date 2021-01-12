package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by tuke on 2018/10/19
 */
@Slf4j
public final class JacksonUtil {

    private JacksonUtil() {
    }

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final YAMLFactory yamlFactory = new YAMLFactory();
    private static final ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);

    static {
        jsonMapper.findAndRegisterModules();
        // disable write three dashes(---) to head
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlMapper.findAndRegisterModules();
        // // write Date as a timestamp
        // yamlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> Map<String, Object> toMap(T t) {
        Map<String, Object> map = jsonMapper.convertValue(t,
                new TypeReference<Map<String, Object>>() {
                });

        return map.entrySet().stream()
                .filter(stringObjectEntry ->
                        stringObjectEntry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Object> readAsMap(String json) {
        try {
            return jsonMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// json

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(File file, Class<T> clazz) {
        try {
            return jsonMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> fromJsonArray(String json) {
        try {
            return jsonMapper.readValue(json, new TypeReference<List<T>>() {
            });
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            return jsonMapper.readValue(json, getGenericType(List.class, clazz));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> List<T> fromJsonArray(File file) {
        try {
            return jsonMapper.readValue(file,
                    new TypeReference<List<T>>() {
                    });
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toJson(Object bean) {
        try {
            return jsonMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// yaml

    public static <T> T fromYaml(String yaml, Class<T> clazz) {
        try {
            //YAMLParser yamlParser = yamlFactory.createParser(yaml);
            //return jsonMapper.readValue(yamlParser, clazz);
            return yamlMapper.readValue(yaml, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T fromYaml(File file, Class<T> clazz) {
        try {
            //YAMLParser yamlParser = yamlFactory.createParser(yaml);
            //return jsonMapper.readValue(yamlParser, clazz);
            return yamlMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toYaml(Object bean) {
        try {
            return yamlMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /// types

    public static JavaType getGenericType(Class<?> genericClass, Class<?>... parameterTypes) {
        return jsonMapper.getTypeFactory().constructParametricType(genericClass, parameterTypes);
    }

}
