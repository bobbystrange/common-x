package org.dreamcat.common.x.asm;

import net.sf.cglib.beans.BeanGenerator;

import java.util.Map;
import java.util.Set;

/**
 * Create by tuke on 2019-05-09
 */
public class BeanGeneratorUtil {

    public static Class<?> generateClass(Map<String, Class<?>> properties) {
        return generateClass(properties, null);
    }

    public static Class<?> generateClass(Map<String, Class<?>> properties, Class<?> superclass) {
        BeanGenerator generator = new BeanGenerator();
        Set<Map.Entry<String, Class<?>>> entrySet = properties.entrySet();
        for (Map.Entry<String, Class<?>> entry : entrySet) {
            String name = entry.getKey();
            Class<?> type = entry.getValue();
            generator.addProperty(name, type);
        }
        if (superclass != null) generator.setSuperclass(superclass);
        return (Class<?>) generator.createClass();
    }

}
