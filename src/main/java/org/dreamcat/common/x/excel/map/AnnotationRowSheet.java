package org.dreamcat.common.x.excel.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dreamcat.common.util.ObjectUtil;
import org.dreamcat.common.x.excel.content.ExcelUnionContent;
import org.dreamcat.common.x.excel.content.IExcelContent;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.dreamcat.common.x.excel.core.IExcelSheet;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelStyle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by tuke on 2020/7/25
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationRowSheet implements IExcelSheet {
    private final Map<Class, MetaCacheLine> metaMap = new HashMap<>();
    private String name;
    private Object scheme;
    private XlsMeta meta;
    private List<Integer> indexes;

    public AnnotationRowSheet(Object scheme) {
        reset(scheme);
    }

    private static boolean isNotListOrArray(Object o) {
        return !(o instanceof List) && !(o.getClass().isArray());
    }

    @SuppressWarnings("rawtypes")
    private static List cast(Object listOrArray) {
        if (listOrArray instanceof Object[]) {
            return Arrays.asList((Object[]) listOrArray);
        } else {
            return (List) listOrArray;
        }
    }

    public void reset(Object scheme) {
        Class clazz = scheme.getClass();
        if (metaMap.containsKey(clazz)) return;

        this.meta = XlsBuilder.parse(scheme);
        checkMetaName(clazz);
        this.indexes = meta.getFieldIndexes();
        this.metaMap.put(clazz, new MetaCacheLine(meta, indexes));

        this.name = meta.name;
        this.scheme = scheme;
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }
    /// static area

    private void checkMetaName(Class clazz) {
        if (ObjectUtil.isEmpty(meta.name)) {
            throw new IllegalArgumentException("sheet name is empty in " + clazz + ", check its annotations");
        }
    }

    @AllArgsConstructor
    static class MetaCacheLine {
        XlsMeta meta;
        List<Integer> indexes;
    }

    @Getter
    class Iter extends ExcelUnionContent implements Iterator<IExcelCell>, IExcelCell {
        XlsMeta subMeta;
        List<Integer> subIndexes;

        List row;
        int schemeSize;
        int schemeIndex;
        int maxRowSpan;
        int offset;

        Object scalar;

        List scalarArray;
        int scalarArraySize;
        int scalarArrayIndex;

        List vector;
        int vectorSize;
        int vectorIndex;

        List<List> vectorArray;
        int vectorArraySize;
        int vectorArrayIndex;
        int vectorArrayColumnSize;
        int vectorArrayColumnIndex;

        int rowIndex;
        int columnIndex;
        int rowSpan;
        int columnSpan;
        ExcelFont font;
        ExcelStyle style;

        Iter() {
            init();
        }

        public void reset(Object scheme) {
            AnnotationRowSheet.this.reset(scheme);

            subMeta = null;
            subIndexes = null;

            scalar = null;

            scalarArray = null;
            scalarArraySize = 0;
            scalarArrayIndex = 0;

            vector = null;
            vectorSize = 0;
            vectorIndex = 0;

            vectorArray = null;
            vectorArraySize = 0;
            vectorArrayIndex = 0;
            vectorArrayColumnSize = 0;
            vectorArrayColumnIndex = 0;

            init();
        }

        private void init() {
            row = meta.getFieldValues(scheme);
            maxRowSpan = 1;
            offset = 0;
            for (Object fieldValue : row) {
                if (fieldValue instanceof List) {
                    maxRowSpan = Math.max(maxRowSpan, ((List) fieldValue).size());
                } else if (fieldValue instanceof Object[]) {
                    maxRowSpan = Math.max(maxRowSpan, ((Object[]) fieldValue).length);
                }
            }

            schemeSize = row.size();
            schemeIndex = 0;
            move();
        }

        @Override
        public IExcelContent getContent() {
            return this;
        }

        @Override
        public boolean hasNext() {
            // empty scheme
            if (schemeSize == 0) return false;
            // reach all schemes
            if (schemeIndex >= schemeSize) return false;
            // has cells
            return scalar != null ||
                    scalarArray != null ||
                    vector != null ||
                    vectorArray != null;
        }

        @Override
        public IExcelCell next() {
            XlsMeta.Cell cell = meta.cells.get(indexes.get(schemeIndex));

            if (scalar != null) {
                // prepare data
                if (cell.serializer == null) {
                    setContent(scalar);
                } else {
                    setContent(cell.serializer.apply(scalar));
                }
                rowIndex = 0;
                columnIndex = offset;
                rowSpan = maxRowSpan;
                columnSpan = cell.span;
                fillFontAndStyle(cell);

                // move
                scalar = null;
                offset += columnSpan;
                schemeIndex++;
                if (schemeIndex < schemeSize) {
                    move();
                }
                return this;
            }

            // in cell case scheme
            if (scalarArray != null) {
                if (cell.serializer == null) {
                    setContent(scalarArray.get(scalarArrayIndex));
                } else {
                    setContent(cell.serializer.apply(scalarArray.get(scalarArrayIndex)));
                }
                rowIndex = scalarArrayIndex;
                columnIndex = offset;
                rowSpan = 1;
                columnSpan = cell.span;
                fillFontAndStyle(cell);

                // move
                scalarArrayIndex++;
                if (scalarArrayIndex >= scalarArraySize) {
                    offset += columnSpan;
                    scalarArray = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                }

                return this;
            }

            if (vector != null) {
                XlsMeta.Cell subCell = subMeta.cells.get(subIndexes.get(vectorIndex));

                if (cell.serializer == null) {
                    setContent(vector.get(vectorIndex));
                } else {
                    setContent(cell.serializer.apply(vector.get(vectorIndex)));
                }
                rowIndex = 0;
                columnIndex = offset++;
                rowSpan = maxRowSpan;
                columnSpan = subCell.span;
                fillFontAndStyle(subCell, cell);

                // move
                vectorIndex++;
                if (vectorIndex >= vectorSize) {
                    vector = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                }
                return this;
            }

            XlsMeta.Cell subCell = subMeta.cells.get(indexes.get(vectorArrayColumnIndex));
            if (cell.serializer == null) {
                setContent(vectorArray.get(vectorArrayIndex).get(vectorArrayColumnIndex));
            } else {
                setContent(cell.serializer.apply(vectorArray.get(vectorArrayIndex).get(vectorArrayColumnIndex)));
            }
            rowIndex = vectorArrayIndex;
            columnIndex = offset + vectorArrayColumnIndex;
            rowSpan = 1;
            columnSpan = subCell.span;
            fillFontAndStyle(subCell, cell);

            // move
            vectorArrayColumnIndex++;
            if (vectorArrayColumnIndex >= vectorArrayColumnSize) {
                vectorArrayColumnIndex = 0;
                vectorArrayIndex++;
                if (vectorArrayIndex >= vectorArraySize) {
                    vectorArray = null;
                    schemeIndex++;
                    if (schemeIndex < schemeSize) {
                        move();
                    }
                } else {
                    vectorArrayColumnSize = vectorArray.get(vectorArrayIndex).size();
                }
            }

            return this;
        }

        // move magical cursor for cells
        private void move() {
            Object fieldValue = row.get(schemeIndex);
            XlsMeta.Cell cell = meta.cells.get(indexes.get(schemeIndex));

            if (!cell.expanded) {
                // s
                if (isNotListOrArray(fieldValue)) {
                    scalar = fieldValue;
                    return;
                }

                // sa
                scalarArray = cast(fieldValue);
                scalarArraySize = scalarArray.size();
                if (scalarArraySize == 0) {
                    throw new IllegalArgumentException("empty list/array field value in " + scheme.getClass());
                }
                scalarArrayIndex = 0;
                return;
            }

            // v
            if (isNotListOrArray(fieldValue)) {
                MetaCacheLine cacheLine = metaMap.computeIfAbsent(
                        fieldValue.getClass(), c -> {
                            XlsMeta meta = XlsBuilder.parse(c, false);
                            return new MetaCacheLine(meta, meta.getFieldIndexes());
                        });
                subMeta = cacheLine.meta;
                subIndexes = cacheLine.indexes;
                vector = subMeta.getFieldValues(fieldValue);
                vectorSize = vector.size();
                vectorIndex = 0;
                return;
            }

            // va
            List rectangle = cast(fieldValue);
            if (rectangle.isEmpty()) {
                throw new IllegalArgumentException("empty list/array field value in " + scheme.getClass());
            }
            MetaCacheLine cacheLine = metaMap.computeIfAbsent(
                    rectangle.get(0).getClass(), c -> {
                        XlsMeta meta = XlsBuilder.parse(c, false);
                        return new MetaCacheLine(meta, meta.getFieldIndexes());
                    });
            subMeta = cacheLine.meta;
            subIndexes = cacheLine.indexes;

            vectorArray = (List<List>) rectangle.stream().map(subMeta::getFieldValues).collect(Collectors.toList());
            vectorArraySize = vectorArray.size();
            vectorArrayIndex = 0;
            vectorArrayColumnSize = vectorArray.get(0).size();
            vectorArrayColumnIndex = 0;
        }

        private void fillFontAndStyle(XlsMeta.Cell cell) {
            font = cell.font != null ? cell.font : meta.defaultFont;
            style = cell.style != null ? cell.style : meta.defaultStyle;
        }

        private void fillFontAndStyle(XlsMeta.Cell subCell, XlsMeta.Cell cell) {
            if (subCell.font != null) {
                font = subCell.font;
            } else if (subMeta.defaultFont != null) {
                font = subMeta.defaultFont;
            } else if (cell.font != null) {
                font = cell.font;
            } else {
                font = meta.defaultFont;
            }

            if (subCell.style != null) {
                style = subCell.style;
            } else if (subMeta.defaultStyle != null) {
                style = subMeta.defaultStyle;
            } else if (cell.style != null) {
                style = cell.style;
            } else {
                style = meta.defaultStyle;
            }
        }
    }
}
