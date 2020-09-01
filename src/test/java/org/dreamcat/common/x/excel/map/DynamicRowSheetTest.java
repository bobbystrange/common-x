package org.dreamcat.common.x.excel.map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.common.x.excel.core.ExcelWorkbook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.dreamcat.common.util.RandomUtil.*;

/**
 * Create by tuke on 2020/8/19
 */
public class DynamicRowSheetTest {

    @Test
    public void test() throws Exception {
        DynamicPojo pojo = newDynamicPojo();
        System.out.println(pojo);

        DynamicRowSheet sheet = new DynamicRowSheet(pojo);

        new ExcelWorkbook<>()
                .add(sheet)
                .writeTo("/Users/tuke/Downloads/book.xlsx");
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DynamicPojo extends XlsMetaTest.Pojo {
        private Map<String, String> map;
        private List<Map<String, String>> mapList;

    }

    public static DynamicPojo newDynamicPojo() {
        DynamicPojo pojo = new DynamicPojo();
        BeanMapUtil.copy(XlsMetaTest.newPojo(), pojo);

        Map<String, String> map = new HashMap<>();
        map.put("a", "map-a-" + choose10(12));
        map.put("b", "map-b-" + choose36(randi(2, 6)));
        map.put("c", "map-c-" + choose72(randi(3, 4)));
        pojo.setMap(map);

        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<>();
            m.put("a", "mapList-a-" + choose10(12));
            m.put("b", "mapList-b-" + choose36(randi(2, 6)));
            m.put("c", "mapList-c-" + choose72(randi(3, 4)));
            mapList.add(m);
        }
        pojo.setMapList(mapList);
        return pojo;
    }
}
