package org.dreamcat.common.x.excel.annotation;

import java.util.function.Function;

/**
 * Create by tuke on 2020/8/10
 */
@SuppressWarnings("rawtypes")
public @interface XlsFormat {

    Class<? extends Function> serializer() default None.class;

    Class<? extends Function> deserializer() default None.class;

    class None implements Function {
        @Override
        public Object apply(Object o) {
            throw new IllegalStateException("this method may not be invoked");
        }
    }
}
