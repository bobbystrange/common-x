package org.dreamcat.common.x.excel.map;

import org.dreamcat.common.x.excel.annotation.XlsCell;
import org.dreamcat.common.x.excel.annotation.XlsFont;
import org.dreamcat.common.x.excel.annotation.XlsRichStyle;
import org.dreamcat.common.x.excel.annotation.XlsSheet;
import org.dreamcat.common.x.excel.annotation.XlsStyle;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelStyle;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Create by tuke on 2020/7/22
 */
@SuppressWarnings("rawtypes")
public class XlsBuilder {

    public static XlsMeta parse(Object object) {
        return parse(object.getClass(), object, true, null);
    }

    public static XlsMeta parse(Class<?> clazz, boolean enableExpanded, Class... requiredTypesInList) {
        return parse(clazz, enableExpanded, new LinkedList<>(Arrays.asList(requiredTypesInList)));
    }

    public static XlsMeta parse(Class<?> clazz, boolean enableExpanded, Collection<Class> requiredTypesInList) {
        return parse(clazz, null, enableExpanded, requiredTypesInList == null ? null : new LinkedList<>(requiredTypesInList));
    }

    public static XlsMeta parse(Class<?> clazz, Object object, boolean enableExpanded, LinkedList<Class> requiredTypesInList) {
        XlsMeta metadata = new XlsMeta();
        Boolean onlyAnnotated = parseXlsSheet(metadata, clazz);
        if (onlyAnnotated == null) return null;

        parseXlsFont(metadata, clazz);
        parseXlsStyle(metadata, clazz);

        Field[] fields = clazz.getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            parseXlsCell(metadata, clazz, object, field, index, onlyAnnotated, enableExpanded, requiredTypesInList);
            parseXlsFont(metadata, field, index);
            parseXlsStyle(metadata, field, index);
            index++;
        }

        return metadata;
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    // true or false to determine whether only annotated fields are processed, null to skip the parsing process
    private static Boolean parseXlsSheet(XlsMeta metadata, Class<?> clazz) {
        XlsSheet xlsSheet = clazz.getDeclaredAnnotation(XlsSheet.class);
        if (xlsSheet != null) {
            metadata.setName(xlsSheet.name());
            return xlsSheet.onlyAnnotated();
        }
        return null;
    }

    private static void parseXlsFont(XlsMeta metadata, Class<?> clazz) {
        XlsFont xlsFont = clazz.getDeclaredAnnotation(XlsFont.class);
        if (xlsFont == null) return;
        metadata.setDefaultFont(ExcelFont.from(xlsFont));
    }

    private static void parseXlsStyle(XlsMeta metadata, Class<?> clazz) {
        XlsStyle xlsStyle = clazz.getDeclaredAnnotation(XlsStyle.class);
        if (xlsStyle == null) return;
        XlsRichStyle xlsRichStyle = clazz.getDeclaredAnnotation(XlsRichStyle.class);
        metadata.setDefaultStyle(ExcelStyle.from(xlsStyle, xlsRichStyle));
    }

    private static void parseXlsFont(XlsMeta metadata, Field field, int index) {
        XlsFont xlsFont = field.getDeclaredAnnotation(XlsFont.class);
        if (xlsFont == null) return;

        XlsMeta.Cell cell = metadata.computeCell(index);

        ExcelFont font = ExcelFont.from(xlsFont);
        cell.setFont(font);
    }

    private static void parseXlsStyle(XlsMeta metadata, Field field, int index) {
        XlsStyle xlsStyle = field.getDeclaredAnnotation(XlsStyle.class);
        XlsRichStyle xlsRichStyle = field.getDeclaredAnnotation(XlsRichStyle.class);
        if (xlsStyle == null && xlsRichStyle == null) return;

        XlsMeta.Cell cell = metadata.computeCell(index);
        ExcelStyle style = ExcelStyle.from(xlsStyle, xlsRichStyle);
        cell.setStyle(style);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    private static void parseXlsCell(XlsMeta metadata, Class<?> clazz, Object object, Field field, int index, boolean onlyAnnotated, boolean enableExpanded, LinkedList<Class> requiredTypesInList) {
        XlsCell xlsCell = field.getDeclaredAnnotation(XlsCell.class);
        if (xlsCell == null) {
            if (onlyAnnotated) return;

            metadata.getCells()
                    .computeIfAbsent(index, it -> new XlsMeta.Cell())
                    .fillDefault(index);
            return;
        }

        if (xlsCell.ignored()) {
            metadata.getCells().put(index, XlsMeta.IGNORED_CELL);
            return;
        }

        XlsMeta.Cell cell = metadata.getCells()
                .computeIfAbsent(index, it -> new XlsMeta.Cell());
        cell.setFieldIndex(index);
        cell.setIndex(xlsCell.index());
        cell.setSpan(xlsCell.span());
        cell.setExpanded(xlsCell.expanded());

        if (!enableExpanded || !xlsCell.expanded()) return;

        Class<?> fieldClass;
        if (object != null) {
            fieldClass = getFieldClass(object, field);
        } else {
            fieldClass = getFieldClass(field, requiredTypesInList);
        }

        cell.setExpandedType(fieldClass);
        if (clazz.equals(fieldClass)) {
            cell.setExpandedMeta(metadata);
        } else {
            XlsMeta fieldMetadata = parse(fieldClass, false, (LinkedList<Class>) null);
            if (fieldMetadata == null) {
                throw new IllegalArgumentException("no @XlsSheet in class " + fieldClass + " on field " + field);
            }
            cell.setExpandedMeta(fieldMetadata);
        }
    }

    private static Class<?> getFieldClass(Field field, LinkedList<Class> requiredTypesInList) {
        Class<?> fieldClass = field.getType();
        if (fieldClass.isAssignableFrom(List.class)) {
            Class requiredType;
            if (requiredTypesInList.isEmpty() || (requiredType = requiredTypesInList.removeFirst()) == null) {
                throw new IllegalArgumentException("require a specified component type in List filed on field " + field);
            }
            fieldClass = requiredType;
        } else if (fieldClass.isArray()) {
            fieldClass = fieldClass.getComponentType();
        }
        return fieldClass;

    }

    private static Class<?> getFieldClass(Object object, Field field) {
        Class<?> fieldClass = field.getType();
        if (fieldClass.isAssignableFrom(List.class)) {
            field.setAccessible(true);
            List fieldValue;
            try {
                fieldValue = (List) (field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (fieldValue.isEmpty()) {
                throw new IllegalArgumentException("require a not-empty value in List filed on field " + field);
            }
            fieldClass = fieldValue.get(0).getClass();
        } else if (fieldClass.isArray()) {
            fieldClass = fieldClass.getComponentType();
        }
        return fieldClass;
    }

}
