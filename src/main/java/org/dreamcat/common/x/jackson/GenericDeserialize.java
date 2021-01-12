package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create by tuke on 2020/7/29
 * <p>
 * String to Numeric, Boolean or Bean
 * Numeric to String, Date, LocalDate or LocalDateTime
 * String/Numeric to
 * {@link java.util.function.Supplier&lt;? extends String&gt;},
 * {@link java.util.function.Supplier&lt;? extends Number&gt;},
 * {@link java.util.function.IntSupplier}
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = GenericDeserializer.class)
public @interface GenericDeserialize {

    // the component of List or Object[]
    Class<?> componentType() default Object.class;

    String zoneId() default "Asia/Shanghai";
}
