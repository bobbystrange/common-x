package com.tukeof.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;

public class ZipArchiver implements Archiver {

    public void archive(File srcFile, File destFile, int level) throws IOException {
        try (ZipArchiveOutputStream outs = new ZipArchiveOutputStream(
                new FileOutputStream(destFile))) {
            outs.setLevel(level);
            archive(srcFile, outs, "");
            outs.flush();
        }
    }

    @Override
    public void archive(File srcFile, File destFile) throws IOException {
        archive(srcFile, destFile, Deflater.DEFAULT_COMPRESSION);
    }

    @Override
    public ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException {
        return new ZipArchiveInputStream(ins);
    }

    @Override
    public ArchiveOutputStream buildArchiveOutputStream(OutputStream outs) {
        return new ZipArchiveOutputStream(outs);
    }

    @Override
    public ArchiveEntry buildArchiveEntry(String name, long size) {
        ZipArchiveEntry entry = new ZipArchiveEntry(name);
        entry.setSize(size);
        return entry;
    }
}
