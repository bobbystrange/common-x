package org.dreamcat.common.x.jackson;

import static org.dreamcat.common.util.BeanUtil.inline;
import static org.dreamcat.common.util.BeanUtil.pretty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/29
 */
public class GenericDeserializeTest {

    @Test
    public void testList() {
        A a = new A(Arrays.asList(1L, 2L, 3L));
        String json = JacksonUtil.toJson(a);
        System.out.println(json);
        A b = JacksonUtil.fromJson(json, A.class);
        System.out.println(inline(b));

        b = JacksonUtil.fromJson(JacksonUtil.toJson(new A()), A.class);
        System.out.println(inline(b));
    }

    @Test
    public void testListInside() {
        String json = "{\"x\":[\"1\",\"2\",\"3\"], \"y\": [6.02E-26, 11529215046068, 1], \"z\": [6.02E-26, 11529215046068, 1]}";
        System.out.println(json);
        B b = JacksonUtil.fromJson(json, B.class);
        System.out.println(pretty(b));
        System.out.println(JacksonUtil.toJson(b));
    }

    @Test
    public void testEnum() {
        C c = new C(
                System.currentTimeMillis(), // x
                new Date(), // y
                LocalDateTime.now().minusHours(1), // z
                Type.Q, // type
                Arrays.asList(Type.P, Type.O, Type.W)); // array

        String json = JacksonUtil.toJson(c);
        System.out.println(json);

        c = JacksonUtil.fromJson(json, C.class);
        System.out.println(JacksonUtil.toJson(c));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class A {

        @JsonDeserialize(using = LongListDeserializer.class)
        @JsonSerialize(using = LongListSerializer.class)
        List<Long> x;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class B {

        @GenericDeserialize(componentType = Long.class)
        @GenericSerialize
        List<Long> x;

        @GenericDeserialize(componentType = Date.class)
        @GenericSerialize
        List<Date> y;

        @GenericDeserialize(componentType = Date.class)
        @GenericSerialize
        List<LocalDateTime> z;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class C {

        @GenericDeserialize
        @GenericSerialize
        Long x;

        @GenericDeserialize
        @GenericSerialize
        Date y;

        @GenericDeserialize
        @GenericSerialize
        LocalDateTime z;

        @GenericDeserialize
        @GenericSerialize
        Type type;

        @GenericDeserialize(componentType = Type.class)
        @GenericSerialize
        List<Type> array;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type implements Supplier<Integer> {
        Q(1), W(2), O(3), P(4);

        private final Integer value;

        @Override
        public Integer get() {
            return value;
        }
    }
}
