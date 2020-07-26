package org.dreamcat.common.x.excel.map;

import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.junit.Test;

import java.util.Arrays;

import static org.dreamcat.common.util.RandomUtil.*;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationListSheetTest {

    @Test
    public void test() throws Exception {
        AnnotationListSheet sheet = new AnnotationListSheet("Sheet One");
        for (int i = 0; i < 12; i++) {
            XlsMetaTest.Pojo pojo = new XlsMetaTest.Pojo(
                    randi(10),
                    Arrays.asList(rand(), rand(), rand()),
                    new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                    Arrays.asList(
                            new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                            new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3)),
                            new XlsMetaTest.Item((long) (randi(1 << 16)), choose26(3))
                    ));
            sheet.add(pojo);
        }

        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent()
            );
        }

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

}
