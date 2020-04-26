package org.dreamcat.common.x.compress;

import org.dreamcat.common.function.ThrowableFunction;
import org.dreamcat.common.util.ByteUtil;
import org.dreamcat.common.util.RandomUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.dreamcat.common.util.ConsoleUtil.print;
import static org.dreamcat.common.util.ConsoleUtil.println;

/**
 * Create by tuke on 2020/4/7
 */
public class CompressorEnumTest {

    @Test
    public void test() throws IOException {
        byte[] data = RandomUtil.choose72(100).getBytes();
        byte[] compressed = GzipUtil.compress(data);
        byte[] uncompressed = GzipUtil.uncompress(compressed);

        println(data.length, new String(data));
        println(compressed.length, ByteUtil.hex(compressed));

        //println(compressed.length, new String(compressed));
        //println(compressed.length);
        println(uncompressed.length, new String(uncompressed));
    }

    @Test
    public void testRatio() throws Exception {
        List<ThrowableFunction<byte[], byte[]>> fns = new ArrayList<>();
        fns.add(CompressorEnum.BZ2::compress);
        fns.add(CompressorEnum.FramedLZ4::compress);
        fns.add(CompressorEnum.FramedSnappy::compress);
        fns.add(CompressorLevelEnum.Gzip::compress);
        fns.add(CompressorLevelEnum.Deflate::compress);

        for (int i = 1; i < 1000; i++) {
            byte[] data = RandomUtil.choose72(i).getBytes();
            print(i + "\t");
            for (ThrowableFunction<byte[], byte[]> fn : fns) {
                byte[] compressed = fn.apply(data);
                //byte[] uncompressed = SnappyUtil.uncompress(compressed);
                print(compressed.length + " ");
            }
            print("\n");
        }
    }

}