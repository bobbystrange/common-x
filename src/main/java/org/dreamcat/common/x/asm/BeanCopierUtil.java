package org.dreamcat.common.x.asm;

import net.sf.cglib.beans.BeanCopier;
import org.dreamcat.common.util.ReflectUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by tuke on 2020/3/3
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class BeanCopierUtil {

    private BeanCopierUtil(){
    }

    // Note that it may cause OOM when coping too many dynamic classes
    private static Map<Class, Map<Class, BeanCopier>> cache = new ConcurrentHashMap<>();

    public static synchronized void evictCache() {
        cache.clear();
        cache = new ConcurrentHashMap<>();
    }

    public static <S> S copy(S source) {
        if (source == null) return null;
        return (S) copy(source, source.getClass());
    }

    public static <S, T> T copy(S source, Class<T> targetClass) {
        T target = ReflectUtil.newInstance(targetClass);
        copy(source, target);
        return target;
    }

    // I passed useConverter=true, then primitive classes will convert to boxed classes automatically
    public static <S, T> void copy(S source, T target) {
        copy(source, target, true);
    }

    /**
     * use cglib to copy properties of beans
     *
     * @param source       source object which will be read
     * @param target       target object which will be writen
     * @param useConverter whether use the default converter or not
     * @param <S>          source class
     * @param <T>          target class
     * @throws IllegalAccessError maybe raised by accessing inner class
     */
    public static <S, T> void copy(S source, T target, boolean useConverter) {
        if (source == null || target == null) return;

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        BeanCopier copier = cache.computeIfAbsent(sourceClass,
                it -> new ConcurrentHashMap<>()).computeIfAbsent(targetClass,
                it -> BeanCopier.create(sourceClass, targetClass, useConverter));
        copier.copy(source, target, BeanCopierUtil::convert);
    }

    private static Object convert(Object sourceFieldValue, Class targetFieldClass, Object targetFieldSetterName) {
        return ReflectUtil.cast(sourceFieldValue, targetFieldClass);
    }

}
