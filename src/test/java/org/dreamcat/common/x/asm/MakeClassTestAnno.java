package org.dreamcat.common.x.asm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create by tuke on 2020/5/28
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MakeClassTestAnno {

    String value() default "";
}
