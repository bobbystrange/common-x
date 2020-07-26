package org.dreamcat.common.x.excel.map;

import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.dreamcat.common.x.excel.core.IExcelCell;
import org.junit.Test;

import java.util.Arrays;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.randi;
import static org.dreamcat.common.x.excel.map.XlsMetaTest.Item;
import static org.dreamcat.common.x.excel.map.XlsMetaTest.Pojo;

/**
 * Create by tuke on 2020/7/26
 */
public class AnnotationRowSheetTest {

    @Test
    public void test() throws Exception {
        Pojo pojo = new Pojo(
                1,
                Arrays.asList(1.0, 2.0, 3.0),
                new Item((long) (randi(1 << 16)), choose26(3)),
                Arrays.asList(
                        new Item((long) (randi(1 << 16)), choose26(3)),
                        new Item((long) (randi(1 << 16)), choose26(3)),
                        new Item((long) (randi(1 << 16)), choose26(3))
                ));

        AnnotationRowSheet sheet = new AnnotationRowSheet(pojo);
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
