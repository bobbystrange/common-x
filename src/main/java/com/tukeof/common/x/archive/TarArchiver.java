package com.tukeof.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

public class TarArchiver implements Archiver {
    @Override
    public ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException {
        return new TarArchiveInputStream(ins);
    }

    @Override
    public ArchiveOutputStream buildArchiveOutputStream(OutputStream outs) {
        return new TarArchiveOutputStream(outs);
    }

    @Override
    public ArchiveEntry buildArchiveEntry(String name, long size) {
        TarArchiveEntry entry = new TarArchiveEntry(name);
        entry.setSize(size);
        return entry;
    }
}
