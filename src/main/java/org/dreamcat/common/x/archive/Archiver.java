package org.dreamcat.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.dreamcat.common.io.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Archiver {

    int BUFFER_SIZE = 4096;

    default void unarchive(File srcFile, File destFile) throws IOException, ArchiveException {
        try (ArchiveInputStream ins = buildArchiveInputStream(
                new FileInputStream(srcFile))) {
            unarchive(destFile, ins);
        }
    }

    default void unarchive(File destFile, ArchiveInputStream ins) throws IOException {
        ArchiveEntry entry;
        while ((entry = ins.getNextEntry()) != null) {
            File dirFile = new File(destFile.getPath() + File.separator + entry.getName());
            FileUtil.mkdir(dirFile);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                unarchiveFile(dirFile, ins);
            }
        }
    }

    default void unarchiveFile(File destFile, ArchiveInputStream ins) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile))) {
            int count;
            byte[] data = new byte[BUFFER_SIZE];
            while ((count = ins.read(data, 0, BUFFER_SIZE)) != -1) {
                bos.write(data, 0, count);
            }
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    default void archive(File srcFile, File destFile) throws IOException {
        try (ArchiveOutputStream outs = buildArchiveOutputStream(
                new FileOutputStream(destFile))) {
            archive(srcFile, outs, "");
            outs.flush();
        }
    }

    default void archive(File srcFile, ArchiveOutputStream outs, String basePath)
            throws IOException {
        if (srcFile.isDirectory()) {
            archiveDir(srcFile, outs, basePath);
        } else {
            archiveFile(srcFile, outs, basePath);
        }
    }

    default void archiveDir(File dir, ArchiveOutputStream outs, String basePath)
            throws IOException {
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            ArchiveEntry entry = buildArchiveEntry(basePath + dir.getName() + "/", 0);

            outs.putArchiveEntry(entry);
            outs.closeArchiveEntry();
            return;
        }

        for (File file : files) {
            archive(file, outs, basePath + dir.getName() + "/");
        }
    }

    default void archiveFile(File file, ArchiveOutputStream outs, String basePath)
            throws IOException {
        ArchiveEntry entry = buildArchiveEntry(basePath + file.getName(), file.length());
        outs.putArchiveEntry(entry);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                outs.write(data, 0, count);
            }
        }

        outs.closeArchiveEntry();
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException;

    ArchiveOutputStream buildArchiveOutputStream(OutputStream outs);

    ArchiveEntry buildArchiveEntry(String name, long size);

}
