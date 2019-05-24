package org.dreamcat.common.x.compress;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Compressor {

    int BUFFER_SIZE = 1024;

    // compress
    default byte[] compress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                compress(ins, outs);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    default void compress(InputStream ins, OutputStream outs) throws IOException {
        try (CompressorOutputStream cos = buildCompressorOutputStream(outs)) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = ins.read(data)) != -1) {
                cos.write(data, 0, count);
            }
        }
    }

    default void compress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                compress(fis, fos);
            }
        }
    }

    default void compress(File srcFile) throws IOException {
        compress(srcFile, new File(srcFile + "." + suffixName()));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    // decompress
    default byte[] decompress(byte[] data) throws IOException {
        try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
            try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                decompress(ins, outs);
                data = outs.toByteArray();
                return data;
            }
        }
    }

    default void decompress(InputStream ins, OutputStream outs) throws IOException {
        try (CompressorInputStream cis = buildCompressorInputStream(ins)) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = cis.read(data)) != -1) {
                outs.write(data, 0, count);
            }
        }
    }

    default void decompress(File srcFile, File destFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcFile)) {
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                decompress(fis, fos);
            }
        }
    }

    default void decompress(File srcFile) throws IOException {
        String destPath = srcFile.getAbsolutePath();
        destPath = destPath.substring(0, destPath.length() - suffixName().length() - 1);
        decompress(srcFile, new File(destPath));
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    CompressorOutputStream buildCompressorOutputStream(OutputStream outs) throws IOException;

    CompressorInputStream buildCompressorInputStream(InputStream ins) throws IOException;

    String suffixName();

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    interface LevelCompressor<T> extends Compressor {

        default byte[] compress(byte[] data, T level) throws IOException {
            try (ByteArrayInputStream ins = new ByteArrayInputStream(data)) {
                try (ByteArrayOutputStream outs = new ByteArrayOutputStream()) {
                    compress(ins, outs, level);
                    data = outs.toByteArray();
                    return data;
                }
            }
        }

        default void compress(InputStream ins, OutputStream outs, T level) throws IOException {
            try (CompressorOutputStream cos = buildCompressorOutputStream(outs, level)) {
                int count;
                byte data[] = new byte[BUFFER_SIZE];
                while ((count = ins.read(data)) != -1) {
                    cos.write(data, 0, count);
                }
            }
        }

        default void compress(File srcFile, File destFile, T level) throws IOException {
            try (FileInputStream fis = new FileInputStream(srcFile)) {
                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    compress(fis, fos, level);
                }
            }
        }

        CompressorOutputStream buildCompressorOutputStream(OutputStream outs, T level) throws IOException;

    }



}
