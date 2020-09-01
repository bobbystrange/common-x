package org.dreamcat.common.x.asm;

import org.dreamcat.common.util.ReflectUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by tuke on 2020/8/11
 */
public class BeanGeneratorUtilTest {

    @Test
    public void generateClass() throws Exception {
        Class<?> clazz = newClass();
        System.out.println(clazz);
        System.out.println(clazz.newInstance());
        System.out.println();
        ReflectUtil.retrieveFields(clazz).forEach(System.out::println);
    }

    public static Class<?> newClass() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("a", int.class);
        map.put("b", Double.class);
        map.put("c", String.class);
        return BeanGeneratorUtil.generateClass(map);
    }
}
