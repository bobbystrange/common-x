package org.dreamcat.common.x.excel.map;

import lombok.Getter;
import lombok.Setter;
import org.dreamcat.common.x.excel.content.IExcelContent;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.dreamcat.common.x.excel.core.IExcelSheet;
import org.dreamcat.common.x.excel.core.IExcelWriteCallback;
import org.dreamcat.common.x.excel.style.ExcelFont;
import org.dreamcat.common.x.excel.style.ExcelStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Create by tuke on 2020/7/26
 */
@Getter
@SuppressWarnings({"rawtypes", "unchecked"})
public class AnnotationListSheet implements IExcelSheet {
    private final String name;
    // [Sheet..., T1..., Sheet..., T2...], it mixes Sheet & Pojo up
    private final List schemes;
    @Setter
    private IExcelWriteCallback writeCallback;

    public AnnotationListSheet(String name) {
        this(name, new ArrayList<>(0));
    }

    public AnnotationListSheet(String name, List schemes) {
        this.name = name;
        this.schemes = schemes;
    }

    public void add(Object row) {
        schemes.add(row);
    }

    public void addAll(Collection scheme) {
        schemes.addAll(scheme);
    }

    public void add(IExcelSheet sheet) {
        schemes.add(sheet);
    }

    @Override
    public Iterator<IExcelCell> iterator() {
        return this.new Iter();
    }

    @Override
    public IExcelWriteCallback writeCallback() {
        return writeCallback;
    }

    @Getter
    private class Iter implements Iterator<IExcelCell>, IExcelCell {
        // as row index offset since row based structure
        int offset;
        int schemeSize;
        int schemeIndex;

        IExcelCell cell;
        int maxRowOffset;
        Iterator<IExcelCell> iterator;
        boolean disableRowSheetIter;
        AnnotationRowSheet.Iter rowSheetIter;

        private Iter() {
            disableRowSheetIter = true;
            schemeSize = schemes.size();
            if (schemeSize == 0) return;
            move();
        }

        @Override
        public IExcelContent getContent() {
            return cell.getContent();
        }

        @Override
        public int getRowIndex() {
            return cell.getRowIndex() + offset;
        }

        @Override
        public int getColumnIndex() {
            return cell.getColumnIndex();
        }

        @Override
        public int getRowSpan() {
            return cell.getRowSpan();
        }

        @Override
        public int getColumnSpan() {
            return cell.getColumnSpan();
        }

        @Override
        public ExcelStyle getStyle() {
            return cell.getStyle();
        }

        @Override
        public ExcelFont getFont() {
            return cell.getFont();
        }

        @Override
        public boolean hasNext() {
            if (schemeSize == 0) return false;
            if (maxRowOffset < 0) {
                offset -= maxRowOffset;
                maxRowOffset = 0;
            }
            return (iterator != null && iterator.hasNext()) ||
                    (!disableRowSheetIter && rowSheetIter != null && rowSheetIter.hasNext());
        }

        @Override
        public IExcelCell next() {
            if (disableRowSheetIter && iterator != null) {
                // prepare cell
                cell = iterator.next();
                maxRowOffset = Math.max(maxRowOffset, cell.getRowSpan());

                if (iterator.hasNext()) return this;
            }

            if (!disableRowSheetIter && rowSheetIter != null) {
                // prepare cell
                cell = rowSheetIter.next();
                maxRowOffset = Math.max(maxRowOffset, cell.getRowSpan());

                if (rowSheetIter.hasNext()) return this;
            }

            iterator = null;
            schemeIndex++;
            if (schemeIndex < schemeSize) {
                move();
            } else {
                disableRowSheetIter = true;
            }
            return this;
        }

        // move magical cursor for cells
        private void move() {
            maxRowOffset = -maxRowOffset;

            Object rawScheme = schemes.get(schemeIndex);
            if (rawScheme instanceof IExcelSheet) {
                iterator = ((IExcelSheet) rawScheme).iterator();
                disableRowSheetIter = true;
            } else {
                if (rowSheetIter == null) {
                    rowSheetIter = new AnnotationRowSheet(rawScheme).new Iter();
                } else {
                    rowSheetIter.reset(rawScheme);
                }
                disableRowSheetIter = false;
            }
        }
    }

}
