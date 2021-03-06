package org.dreamcat.common.x.asm.copy;

import java.util.function.Supplier;
import net.sf.cglib.beans.BeanCopier;
import org.dreamcat.common.core.Timeit;
import org.dreamcat.common.function.ThrowableSupplier;
import org.dreamcat.common.util.BeanUtil;
import org.dreamcat.common.x.asm.BeanCopierUtil;
import org.dreamcat.common.x.asm.BeanMapUtil;
import org.dreamcat.common.x.asm.BeanMapUtilTest;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

/**
 * Create by tuke on 2020/8/12
 */
public class BeanCopySpeedTest {

    BeanCopier copier = BeanCopier.create(
            BeanMapUtilTest.C.class, BeanMapUtilTest.C.class, false);

    private void speed(
            int repeat, int count, int skip,
            ThrowableSupplier<Object> supplier, Supplier<Object> constructor) {
        String ts = Timeit.ofActions()
                .repeat(repeat)
                .count(count)
                .skip(skip)
                .addUnaryAction(supplier, it -> {
                    BeanUtil.copy(it, constructor.get());
                })
                .addUnaryAction(supplier, it -> {
                    BeanUtils.copyProperties(it, constructor.get());
                })
                .addUnaryAction(supplier, it -> {
                    BeanMapUtil.copy(it, constructor.get());
                })
                .addUnaryAction(supplier, it -> {
                    copier.copy(it, constructor.get(), null);
                })
                .addUnaryAction(supplier, it -> {
                    BeanCopier.create(
                            BeanMapUtilTest.C.class, BeanMapUtilTest.C.class, false)
                            .copy(it, constructor.get(), null);
                })
                .addUnaryAction(supplier, it -> {
                    BeanCopierUtil.copy(it, constructor.get(), false);
                })
                .addUnaryAction(supplier, it -> {
                    BeanCopierUtil.copy(it, constructor.get());
                })
                .runAndFormatMs();
        System.out.printf("[%07d] %s\n", repeat, ts);
    }

    @Test
    public void testSpeedPojo() throws Exception {
        System.out.println("\t\t\t common  spring \t common-x \t copier \t BeanCopierUtil");
        for (int i = 1; i < 1 << 16; i *= 2) {
            speed(i, 10, 2, BeanMapUtilTest::newC, BeanMapUtilTest.C::new);
        }
    }

}
