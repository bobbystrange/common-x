package org.dreamcat.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LZMACompressor implements Compressor {
    private static final String suffixName = "lzma";

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new LZMACompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new LZMACompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }
}
