package com.tukeof.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DeflateCompressor implements Compressor.LevelCompressor<Integer> {

    // Deflater.DEFAULT_COMPRESSION = -1
    @Override public CompressorOutputStream buildCompressorOutputStream(
            OutputStream outs, Integer level) throws IOException {
        DeflateParameters parameters = new DeflateParameters();
        parameters.setCompressionLevel(level);
        return new DeflateCompressorOutputStream(outs, parameters);
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new DeflateCompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new DeflateCompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

    private static final String suffixName = "z";
}
