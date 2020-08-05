package org.dreamcat.common.x.excel.map;

import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationRowSheetTest {

    @Test
    public void test() throws Exception {
        AnnotationRowSheet sheet = new AnnotationRowSheet(XlsMetaTest.newPojo());
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    cell.getFont(), cell.getStyle()
            );
        }

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

}
