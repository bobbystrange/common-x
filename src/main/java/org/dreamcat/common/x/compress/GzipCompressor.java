package org.dreamcat.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GzipCompressor implements Compressor.LevelCompressor<Integer> {

    private static final String suffixName = "gz";

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public CompressorOutputStream buildCompressorOutputStream(
            OutputStream outs, Integer level) throws IOException {
        GzipParameters parameters = new GzipParameters();
        parameters.setCompressionLevel(level);
        return new GzipCompressorOutputStream(outs, parameters);
    }

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        return new GzipCompressorOutputStream(outs);
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new GzipCompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

}
