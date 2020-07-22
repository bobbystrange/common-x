package org.dreamcat.common.x.excel.util;


import org.dreamcat.common.x.excel.core.IExcelCell;
import org.dreamcat.common.x.excel.core.XlsSheet;

import java.lang.annotation.Annotation;

/**
 * Create by tuke on 2020/7/22
 */
public class XlsBuilder {

    public static <T> IExcelCell parse(Class<T> clazz) {
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof XlsSheet) {
                XlsSheet xlsSheet = (XlsSheet) annotation;
            }
        }
        return null;

    }
}
