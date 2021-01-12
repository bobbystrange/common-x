package org.dreamcat.common.x.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Create by tuke on 2020/7/30
 * <p>
 * Boolean to 1/0
 * Long to String
 * Date, LocalDate, LocalDateTime,
 * {@link java.util.function.Supplier&lt;? extends String&gt;},
 * {@link java.util.function.Supplier&lt;? extends Number&gt;},
 * {@link java.util.function.IntSupplier}
 * to Numeric
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = GenericSerializer.class)
public @interface GenericSerialize {

    boolean booleanCast() default false;

    boolean longCast() default true;

    String zoneId() default "Asia/Shanghai";
}
