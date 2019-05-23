package com.tukeof.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SnappyCompressor implements Compressor {

    // SnappyCompressorInputStream.DEFAULT_BLOCK_SIZE = 32 * 1024
    @Override
    public byte[] compress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs, data.length);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    public byte[] compress(byte[] data, int blockSize) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs, data.length, blockSize);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    @Override
    public void compress(InputStream ins, OutputStream outs) throws IOException {
        compress(ins, outs, ins.available());
    }

    public void compress(InputStream ins, OutputStream outs, long uncompressedSize) throws IOException {
        compress(ins, outs, uncompressedSize, SnappyCompressorInputStream.DEFAULT_BLOCK_SIZE);
    }

    public void compress(
            InputStream ins, OutputStream outs,
            long uncompressedSize, int blockSize) throws IOException {
        try (SnappyCompressorOutputStream cos = new SnappyCompressorOutputStream(outs, uncompressedSize, blockSize)) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = ins.read(data)) != -1) {
                cos.write(data, 0, count);
            }
        }
    }

    @Override
    public void compress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos, srcFile.length());
            }
        }
    }

    public void compress(File srcFile, File destFile, int blockSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos, srcFile.length(), blockSize);
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    @Override
    public CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException {
        throw new  NotImplementedException();
    }

    @Override
    public CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException {
        return new SnappyCompressorInputStream(ins);
    }

    @Override
    public String suffixName() {
        return suffixName;
    }

    private static final String suffixName = "snappy";

}
