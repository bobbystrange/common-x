package org.dreamcat.common.x.compress;

import org.dreamcat.common.util.RandomUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.function.IntFunction;

import static org.dreamcat.common.util.PrintUtil.print;
import static org.dreamcat.common.util.PrintUtil.println;

/**
 * Create by tuke on 2020/4/7
 */
public class SnappyUtilTest {

    @Test
    public void test() throws IOException {
        byte[] data = RandomUtil.choose72(100).getBytes();
        byte[] compressed = SnappyUtil.compress(data);
        byte[] uncompressed = SnappyUtil.uncompress(compressed);

        println(data.length, new String(data));
        println(compressed.length, new String(compressed));
        println(uncompressed.length, new String(uncompressed));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRatio() throws IOException {
        IntFunction<String>[] fns = new IntFunction[]{
                RandomUtil::choose10,
                RandomUtil::choose16,
                RandomUtil::choose26,
                RandomUtil::choose36,
                RandomUtil::choose52,
                RandomUtil::choose62,
                RandomUtil::choose72,
        };

        for (int i = 1; i < 1000; i++) {
            print(i + "\t");
            for (IntFunction<String> fn : fns) {
                byte[] data = fn.apply(i).getBytes();
                byte[] compressed = SnappyUtil.compress(data);
                //byte[] uncompressed = SnappyUtil.uncompress(compressed);
                print(compressed.length + " ");
            }
            print("\n");
        }
    }
}
