package org.dreamcat.common.x.excel.map;

import org.dreamcat.common.x.excel.callback.FitWidthWriteCallback;
import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.dreamcat.common.x.excel.util.ExcelBuilderTest;
import org.junit.Test;

import java.util.Arrays;

import static org.dreamcat.common.util.RandomUtil.*;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationListSheetTest {

    @Test
    public void testSmall() throws Exception {
        AnnotationListSheet sheet = new AnnotationListSheet("Sheet One");
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

        sheet.add(ExcelBuilderTest.headerSheet());
        for (IExcelCell cell : sheet) {
            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    cell.getFont(), cell.getStyle()
            );
        }

        sheet.setWriteCallback(new FitWidthWriteCallback());
        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void test() throws Exception {
        AnnotationListSheet sheet = new AnnotationListSheet("Sheet One");
        sheet.add(ExcelBuilderTest.headerSheet());
        sheet.add(ExcelBuilderTest.headerSheet());

        for (int i = 0; i < 6; i++) {
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

        sheet.add(ExcelBuilderTest.headerSheet());

        for (int i = 0; i < 6; i++) {
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
            System.out.printf("[%d, %d, %d, %d] %s\n%s\n%s\n\n",
                    cell.getRowIndex(), cell.getColumnIndex(),
                    cell.getRowSpan(), cell.getColumnSpan(),
                    cell.getContent(),
                    cell.getFont(), cell.getStyle()
            );
        }

        sheet.setWriteCallback(new FitWidthWriteCallback());

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }

}
