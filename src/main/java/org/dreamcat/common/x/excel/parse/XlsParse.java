package org.dreamcat.common.x.excel.parse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Create by tuke on 2020/8/14
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XlsParse {
    /**
     * field locator
     * if name unmatched, then use regexp
     * also unmatched, then use index
     *
     * @return exactly matches field name
     */
    String name() default "";

    String regexp() default "";

    boolean ignored() default false;

    boolean expanded() default false;

    Class<? extends Function<String, Object>> deserializer() default NoneDeserializer.class;

    class NoneDeserializer implements Function<String, Object> {
        @Override
        public Object apply(String o) {
            throw new IllegalStateException("this method may not be invoked");
        }
    }
}
