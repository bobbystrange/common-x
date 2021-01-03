package org.dreamcat.common.x.asm;

import static org.dreamcat.common.util.BeanUtil.pretty;
import static org.dreamcat.common.util.RandomUtil.choose16;
import static org.dreamcat.common.util.RandomUtil.choose36;
import static org.dreamcat.common.util.RandomUtil.choose72;
import static org.dreamcat.common.util.RandomUtil.rand;
import static org.dreamcat.common.util.RandomUtil.randi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dreamcat.common.core.Timeit;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.common.util.RandomUtil;
import org.junit.Test;

/**
 * Create by tuke on 2020/8/11
 */
public class BeanMapUtilTest {

    public static C newC() {
        return new C(
                randi(256, 65536), rand() * 1024, choose72(randi(1, 12)),
                "#" + choose16(6), randi(1 << 16, 1 << 24), rand(),
                System.currentTimeMillis(), (byte) randi(128), choose36(randi(1, 6)));
    }

    @Test
    public void beforeSpeed() {
        Map<String, Object> m = BeanMapUtil.toMap(newC());
        System.out.println(pretty(m));

        C c = BeanMapUtil.fromMap(m, C.class);
        System.out.println(pretty(c));

        m = BeanUtil.toMap(newC());
        System.out.println(pretty(m));

        c = BeanUtil.fromMap(m, C.class);
        System.out.println(pretty(c));
    }

    @Test
    public void speed() {
        System.out
                .println("  \t\t  asm(fromMap) \t  reflect(fromMap) asm(toMap) \t  reflect(toMap)");
        for (int i = 1; i <= 1024; i *= 5) {
            long[] ts = Timeit.ofActions()
                    .addUnaryAction(() -> BeanMapUtil.toMap(newC()), it -> {
                        BeanMapUtil.fromMap(it, C.class);
                    })
                    .addUnaryAction(() -> BeanMapUtil.toMap(newC()), it -> {
                        BeanUtil.fromMap(it, C.class);
                    })
                    .addUnaryAction(BeanMapUtilTest::newC,
                            BeanMapUtil::toMap)
                    .addUnaryAction(BeanMapUtilTest::newC,
                            BeanUtil::toMap)
                    .count(10).skip(2).repeat(i).run();
            String s = Arrays.stream(ts).mapToObj(it -> String.format("%6.3fms", it / 1000_000.))
                    .collect(Collectors.joining(" \t\t "));
            System.out.printf("%05d \t %s\n", i, s);
        }
    }

    @Test
    public void toMap() {
        Class<?> clazz = BeanGeneratorUtilTest.newClass();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("a", 1);
        objectMap.put("b", 3.14);
        objectMap.put("c", RandomUtil.uuid());

        Object o = BeanMapUtil.fromMap(objectMap, clazz);
        assert o != null;
        assert clazz.equals(o.getClass());
        System.out.println(pretty(o));
        System.out.println();

        objectMap = BeanMapUtil.toMap(o);
        System.out.println(pretty(objectMap));
        System.out.println();

        objectMap = BeanUtil.toMap(o);
        System.out.println(pretty(objectMap));
    }

    @Test
    public void toList() {
        C c = newC();
        System.out.println(pretty(c));
        System.out.println();

        List<Object> list = BeanMapUtil.toList(c);
        System.out.println(pretty(list));
        System.out.println();

        Map<String, Object> map = BeanMapUtil.toMap(c);
        System.out.println(pretty(map));
    }

    @Getter
    @Setter
    public static class A {

        int x1;
        Double x2;
        String x3;
    }

    @Getter
    @Setter
    public static class B extends A {

        String y1;
        int y2;
        Double y3;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class C extends B {

        long z1;
        Byte z2;
        String z3;

        public C(int x1, Double x2, String x3, String y1, int y2, Double y3, long z1, Byte z2,
                String z3) {
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;

            this.y1 = y1;
            this.y2 = y2;
            this.y3 = y3;

            this.z1 = z1;
            this.z2 = z2;
            this.z3 = z3;
        }
    }

}
