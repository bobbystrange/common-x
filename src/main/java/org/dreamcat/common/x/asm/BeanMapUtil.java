package org.dreamcat.common.x.asm;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by tuke on 2020/6/5
 */
@Slf4j
@SuppressWarnings({"unchecked"})
public class BeanMapUtil {

    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.forEach((k, v) -> {
            map.put(k.toString(), v);
        });
        return map;
    }

    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage());
            return null;
        }

        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }
}
