package org.dreamcat.common.x.excel.wrapped;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dreamcat.common.x.excel.core.ExcelCell;
import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.junit.Test;

import java.util.ArrayList;

import static org.dreamcat.common.util.RandomUtil.*;
import static org.dreamcat.common.x.excel.util.ExcelBuilder.term;

/**
 * Create by tuke on 2020/7/22
 */
public class WrappedListSheetTest {

    @Test
    public void exportSmall() throws Exception {
        WrappedListSheet sheet = new WrappedListSheet("Sheet One");
        sheet.add(new ExcelCell(term("A1:C2"), 0, 0, 2, 3));
        sheet.add(new ExcelCell(term("D1:E3"), 0, 3, 3, 2));
        sheet.add(new ExcelCell(term("B3:C3"), 2, 1, 1, 2));
        sheet.add(new ExcelCell(term("A3"), 2, 0));
        sheet.add(new Pojo(randi(10), rand(), null, choose72(6)));
        sheet.add(new Pojo(randi(1 << 16), rand() * (1 << 16), null, choose72(2)));
        ExcelWorkbook<WrappedListSheet> book = new ExcelWorkbook<WrappedListSheet>();
        book.getSheets().add(sheet);
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Test
    public void exportWrappedListSheet() throws Exception {
        WrappedListSheet sheet = new WrappedListSheet("Sheet One");
        // list1
        sheet.add(new ExcelCell(term("A1:C2"), 0, 0, 2, 3));
        sheet.add(new ExcelCell(term("D1:E3"), 0, 3, 3, 2));
        sheet.add(new ExcelCell(term("B3:C3"), 2, 1, 1, 2));
        sheet.add(new ExcelCell(term("A3"), 2, 0));

        // list2
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            pojoList.add(new Pojo(randi(10), rand(), null, choose26(6)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        // list3
        pojoList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            pojoList.add(new Pojo(randi(1 << 16), rand() * (1 << 16), null, choose26(2)));
            System.out.println(pojoList.get(pojoList.size() - 1));
        }
        sheet.addAll(pojoList);

        ExcelWorkbook<WrappedListSheet> book = new ExcelWorkbook<WrappedListSheet>();
        book.getSheets().add(sheet);
        book.writeTo("/Users/tuke/Downloads/book.xlsx");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Pojo {
        int a;
        double b;
        Long c;
        String s;
    }

}
