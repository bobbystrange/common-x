package org.dreamcat.common.x.archive;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZMethod;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.dreamcat.common.x.archive.Archiver.recurseMkDir;

public class SevenZArchiver {
    private static final int BUFFER_SIZE = 4096;

    public void archive(File srcFile, File destFile, SevenZMethod method) throws IOException {
        try (SevenZOutputFile outs = new SevenZOutputFile(destFile)) {
            outs.setContentCompression(method);
            archive(srcFile, outs, "");
        }
    }

    public void archive(File srcFile, File destFile) throws IOException {
        archive(srcFile, destFile, SevenZMethod.LZMA2);
    }

    public void archive(File srcFile, SevenZOutputFile outs, String basePath)
            throws IOException {
        if (srcFile.isDirectory()) {
            archiveDir(srcFile, outs, basePath);
        } else {
            archiveFile(srcFile, outs, basePath);
        }
    }

    public void archiveDir(File dir, SevenZOutputFile outs, String basePath)
            throws IOException {
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName(basePath + dir.getName() + "/");
            outs.putArchiveEntry(entry);
            outs.closeArchiveEntry();
            return;
        }

        for (File file : files) {
            archive(file, outs, basePath + dir.getName() + "/");
        }
    }

    public void archiveFile(File file, SevenZOutputFile outs, String basePath)
            throws IOException {
        SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName(basePath + file.getName());
        entry.setSize(file.length());
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

    public void unarchive(File srcFile, File destFile) throws IOException {
        try (SevenZFile ins = new SevenZFile(srcFile)) {
            unarchive(destFile, ins);
        }
    }

    public void unarchive(File destFile, SevenZFile ins) throws IOException {
        SevenZArchiveEntry entry;
        while ((entry = ins.getNextEntry()) != null) {
            File dirFile = new File(destFile.getPath() + File.separator + entry.getName());
            recurseMkDir(dirFile);

            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                unarchiveFile(dirFile, ins);
            }
        }
    }

    public void unarchiveFile(File destFile, SevenZFile ins) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(destFile))) {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            while ((count = ins.read(data, 0, BUFFER_SIZE)) != -1) {
                bos.write(data, 0, count);
            }
        }
    }

}
