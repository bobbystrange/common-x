package com.tukeof.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FramedLZ4Compressor implements
        Compressor.LevelCompressor<FramedLZ4CompressorOutputStream.BlockSize> {

    // BlockSize.M4
    @Override
    public CompressorOutputStream buildCompressorOutputStream(
            OutputStream outs, FramedLZ4CompressorOutputStream.BlockSize blockSize) throws IOException {
        FramedLZ4CompressorOutputStream.Parameters params =
                new FramedLZ4CompressorOutputStream.Parameters(blockSize);
        return new FramedLZ4CompressorOutputStream(outs, params);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new FramedLZ4CompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new FramedLZ4CompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

    private static final String suffixName = "lz4";
}
