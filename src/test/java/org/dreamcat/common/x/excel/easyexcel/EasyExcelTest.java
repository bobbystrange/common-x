package org.dreamcat.common.x.excel.easyexcel;

import static org.dreamcat.common.util.RandomUtil.choose26;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

/**
 * Create by tuke on 2020/7/24
 */
public class EasyExcelTest {

    @Test
    public void test() {
        ArrayList<Pojo> pojoList;
        pojoList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            pojoList.add(new Pojo(randi(10), rand(),
                    randi(2) == 1 ? null : (long) randi(1 << 12, 1 << 13),
                    choose26(6)));
        }

        File tmpFile = new File("/Users/tuke/Downloads/easy_excel.xlsx");

        ExcelWriter excelWriter = EasyExcel.write(tmpFile, Pojo.class)
                .head(Arrays.asList(
                        Arrays.asList("qw", "a"), Arrays.asList("b"),
                        Arrays.asList("ZXC", "c"), Arrays.asList("d")))
                .useDefaultStyle(false)
                .build();

        WriteSheet writeSheet = EasyExcel.writerSheet("Sheet One").build();
        excelWriter.write(pojoList, writeSheet);
        excelWriter.finish();
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
