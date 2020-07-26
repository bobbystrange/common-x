package org.dreamcat.common.x.excel.map;

import lombok.Data;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelStyle;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/24
 */
@Data
public class XlsMeta {
    // internal use only, for ignored filed
    public static final Cell IGNORED_CELL = new Cell();
    // @XlsSheet
    String name;
    ExcelFont defaultFont;
    ExcelStyle defaultStyle;
    Map<Integer, Cell> cells;

    public XlsMeta() {
        this.cells = new HashMap<>();
    }

    private static List<Cell> sortCells(Map<Integer, Cell> cells) {
        return cells.values().stream()
                .sorted(Comparator.comparingInt(XlsMeta.Cell::getFieldIndex))
                .collect(Collectors.toList());
    }

    public boolean isExpanded(int index) {
        Cell cell = this.cells.get(index);
        return cell != null && cell.expanded;
    }

    public Cell computeCell(int index) {
        Cell cell = cells.get(index);
        if (cell != null && cell.equals(XlsMeta.IGNORED_CELL)) return null;

        if (cell == null) {
            cell = new XlsMeta.Cell();
            cells.put(index, cell);
        }
        return cell;
    }

    public List<Integer> getFieldIndexes() {
        return cells.keySet().stream().sorted().collect(Collectors.toList());
    }

    @SuppressWarnings("rawtypes")
    public List getFieldValues(Object row) {
        Field[] fields = row.getClass().getDeclaredFields();
        List<XlsMeta.Cell> sortedCells = sortCells(cells);

        List<Object> fieldValues = new ArrayList<>();
        for (XlsMeta.Cell cell : sortedCells) {
            Field field = fields[cell.fieldIndex];
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(row);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            fieldValues.add(value);
        }
        return fieldValues;
    }

    // @XlsCell
    @Data
    public static class Cell {
        int fieldIndex;

        int index;
        int span;
        boolean expanded;

        ExcelFont font;
        ExcelStyle style;
        // only not null if expended on a array field
        Class<?> expandedType;
        XlsMeta expandedMeta;

        public void fillDefault(int fieldIndex) {
            this.fieldIndex = fieldIndex;
            index = -1;
            span = 1;
        }
    }
}
