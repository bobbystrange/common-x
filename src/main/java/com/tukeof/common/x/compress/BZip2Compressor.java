package com.tukeof.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BZip2Compressor implements Compressor.LevelCompressor<Integer> {

    // MAX_BLOCKSIZE = 9
    @Override
    public CompressorOutputStream buildCompressorOutputStream(
            OutputStream outs, Integer blockSize) throws IOException {
        return new BZip2CompressorOutputStream(outs, blockSize);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new BZip2CompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new BZip2CompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

    private static final String suffixName = "bz2";
}
