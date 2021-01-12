package org.dreamcat.common.x.jackson;

import static org.dreamcat.common.util.BeanUtil.inline;
import static org.dreamcat.common.util.BeanUtil.pretty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.x.test.BeanData;
import org.junit.Test;


/**
 * Create by tuke on 2020/2/26
 */
public class JacksonUtilTest {

    @Test
    public void toTest() {
        Object obj = BeanData.ofPojo();
        System.out.println(pretty(obj));
        String json = JacksonUtil.toJson(obj);
        System.out.println(json);

        Object obj2 = JacksonUtil.fromJson(json, BeanData.Pojo.class);
        System.out.println(pretty(obj2));
        String json2 = JacksonUtil.toJson(obj2);
        System.out.println(json2);

        Map<String, Object> map = JacksonUtil.toMap(obj);
        System.out.println();
        System.out.println("map");
        System.out.println(pretty(map));

        System.out.println(JacksonUtil.fromJson("\"1\"", Long.class));
    }

    @Test
    public void fromTest() {
        List<Integer> list = JacksonUtil.fromJsonArray("[1, 2, 3]", Integer.class);
        System.out.println(list);
        System.out.println(JacksonUtil.fromJsonArray("[{\"a\": 1, \"b\": true}]"));
        System.out.println(JacksonUtil.fromJsonArray("[1, 2E2, 3.14]", Double.class));
    }

    @Data
    static class VarV {

        int a;
        String b;
    }

    @Test
    public void arrayFromTest() throws JsonProcessingException {
        List<Integer> list1 = JacksonUtil.fromJsonArray("[1.0, 2.0, 3.0]");
        System.out.println(list1);
        try {
            System.out.println(list1.get(0).getClass());
        } catch (ClassCastException e) {
            System.err.println("list1: " + e.getMessage());
        }

        String json = "[{\"a\": 1, \"b\": \"\"}]";

        List<VarV> list2 = JacksonUtil.fromJsonArray(json);
        System.out.println(list2);
        try {
            VarV var1 = list2.get(0);
        } catch (ClassCastException e) {
            System.err.println("list2: " + e.getMessage());
        }

        List<VarV> list3 = new ObjectMapper().readValue(json,
                new TypeReference<List<VarV>>() {
                });
        try {
            VarV var1 = list3.get(0);
            System.out.println(var1.getClass());
        } catch (ClassCastException e) {
            System.err.println("list3: " + e.getMessage());
        }

        List<VarV> list4 = fromJsonArray(json, VarV.class);
        System.out.println(list4);
        try {
            VarV var1 = list4.get(0);
            System.out.println("list4: " + list4);
        } catch (ClassCastException e) {
            System.err.println("list4: " + e.getMessage());
        }

    }

    @Test
    public void edgeTest() {
        System.out.println();
        System.out.println(JacksonUtil.toJson("a string"));
        System.out.println(JacksonUtil.toJson(1024));
        System.out.println(JacksonUtil.toJson(3.14));
        System.out.println(JacksonUtil.toJson(true));
        System.out.println(JacksonUtil.toJson(new Date()));

        System.out.println(JacksonUtil.fromJson("a string", String.class));
    }

    @Test
    public void yamlTest() {
        Object obj = BeanData.ofPojo();
        String yaml = JacksonUtil.toYaml(obj);
        System.out.println(yaml);

        Object obj2 = JacksonUtil.fromYaml(yaml, BeanData.Pojo.class);
        String yaml2 = JacksonUtil.toYaml(obj2);
        System.out.println();
        System.out.println(yaml2);
    }

    // @Test
    // public void testSpeed() {
    //     println("\t \t common\t\t cglib");
    //     for (int i = 1; i < (1 << 15); i *= 2) {
    //         String ts = Timeit.ofActions()
    //                 .addUnaryAction(BeanData::ofPojo, BeanMapUtil::toMap)
    //                 .addUnaryAction(BeanData::ofPojo,
    //                         org.dreamcat.common.web.asm.BeanMapUtil::toMap)
    //                 .count(10)
    //                 .skip(2)
    //                 .repeat(i)
    //                 .runAndFormatUs();
    //         printf("%4d \t %s\n", i, ts);
    //     }
    // }

    @Test
    public void testList() {
        LongListCase a = new LongListCase(Arrays.asList(1L, 2L, 3L));
        String json = JacksonUtil.toJson(a);
        System.out.println(json);
        LongListCase b = JacksonUtil.fromJson(json, LongListCase.class);
        System.out.println(inline(b));

        b = JacksonUtil.fromJson(JacksonUtil.toJson(new LongListCase()), LongListCase.class);
        System.out.println(inline(b));

        // inside

        LongListCase2 a2 = new LongListCase2(Arrays.asList(1L, 2L, 3L));
        String json2 = JacksonUtil.toJson(a);
        System.out.println(json);
        LongListCase2 b2 = JacksonUtil.fromJson(json2, LongListCase2.class);
        System.out.println(inline(b2));

        b2 = JacksonUtil.fromJson(JacksonUtil.toJson(new LongListCase2()), LongListCase2.class);
        System.out.println(inline(b2));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class LongListCase {

        @JsonDeserialize(using = LongListDeserializer.class)
        @JsonSerialize(using = LongListSerializer.class)
        List<Long> a;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class LongListCase2 {

        @JsonDeserialize(using = LongDeserializer.class)
        @JsonSerialize(using = LongSerializer.class)
        List<Long> a;
    }

    private static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(
                    json, new TypeReference<List<T>>() {
                    });
        } catch (IOException e) {
            return null;
        }
    }
}
