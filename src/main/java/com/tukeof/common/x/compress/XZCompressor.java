package com.tukeof.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XZCompressor implements Compressor {
    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new XZCompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new XZCompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

    private static final String suffixName = "xz";
}
