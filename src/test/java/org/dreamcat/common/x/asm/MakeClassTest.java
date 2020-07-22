package org.dreamcat.common.x.asm;

import lombok.extern.slf4j.Slf4j;
import org.dreamcat.common.util.RandomUtil;
import org.junit.Test;

/**
 * Create by tuke on 2020/5/28
 */
@Slf4j
public class MakeClassTest {

    @Test
    @SuppressWarnings("unchecked")
    public void toClassTest() throws Exception {
        String className = "org.dreamcat.common.web.asm.A$" + RandomUtil.uuid32();
        Class<? extends MakeClassTestBase> clazz = (Class<? extends MakeClassTestBase>)
                MakeClass.make(className)
                        .superClass(MakeClassTestBase.class)
                        .annotation(MakeClassTestAnno.class, "value", "test")
                        .toClass();

        MakeClassTestAnno anno = clazz.getAnnotation(MakeClassTestAnno.class);
        assert anno != null;
        log.info("{}", anno.value());
        Class<?> loadedClass = Class.forName(className);
        assert clazz.equals(loadedClass);
    }
}
