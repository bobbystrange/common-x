package org.dreamcat.common.x.asm;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by tuke on 2020/8/11
 */
public class BeanGeneratorUtilTest {

    @Test
    public void generateClass() throws Exception {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("a", int.class);
        map.put("b", Double.class);
        map.put("c", String.class);
        Class<?> clazz = BeanGeneratorUtil.generateClass(map);
        System.out.println(clazz);
        System.out.println(clazz.newInstance());
    }
}
