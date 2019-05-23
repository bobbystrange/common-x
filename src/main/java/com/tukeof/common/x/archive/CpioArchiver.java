package com.tukeof.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

public class CpioArchiver implements Archiver {
    @Override
    public ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException {
        return new CpioArchiveInputStream(ins);
    }

    @Override
    public ArchiveOutputStream buildArchiveOutputStream(OutputStream outs) {
        return new CpioArchiveOutputStream(outs);
    }

    @Override
    public ArchiveEntry buildArchiveEntry(String name, long size) {
        CpioArchiveEntry entry = new CpioArchiveEntry(name);
        entry.setSize(size);
        return entry;
    }
}
