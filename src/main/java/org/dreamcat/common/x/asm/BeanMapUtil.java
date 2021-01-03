package org.dreamcat.common.x.asm;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanMap;
import org.dreamcat.common.util.ReflectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by tuke on 2020/6/5
 */
@Slf4j
@SuppressWarnings({"unchecked"})
public final class BeanMapUtil {

    private BeanMapUtil(){
    }

    /**
     * use Getter/Setter or public field
     *
     * @param bean record object
     * @return fieldName -> fieldValue
     */
    public static Map<String, Object> toMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.forEach((k, v) -> map.put(k.toString(), v));
        return map;
    }

    /**
     * use Getter/Setter or public field
     *
     * @param map   record class
     * @param clazz record class
     * @param <T>   record type
     * @return fieldName -> fieldValue
     */
    public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
        T bean = ReflectUtil.newInstance(clazz);
        BeanMap beanMap = BeanMap.create(bean);
        beanMap.putAll(map);
        return bean;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static List<Object> toList(Object bean) {
        BeanMap beanMap = BeanMap.create(bean);
        return new ArrayList<Object>(beanMap.values());
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static <T> T copy(T bean, Class<T> clazz) {
        T targetBean = ReflectUtil.newInstance(clazz);
        copy(bean, targetBean);
        return targetBean;
    }

    public static <T> void copy(T source, T target) {
        BeanMap sourceBeanMap = BeanMap.create(source);
        BeanMap targetBeanMap = BeanMap.create(target);
        targetBeanMap.putAll(sourceBeanMap);
    }

}
