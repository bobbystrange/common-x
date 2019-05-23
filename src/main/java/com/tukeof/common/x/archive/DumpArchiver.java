package com.tukeof.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.InputStream;
import java.io.OutputStream;

// only unarchive is support
public class DumpArchiver implements Archiver {
    @Override
    public ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException {
        return new DumpArchiveInputStream(ins);
    }

    @Override
    public ArchiveOutputStream buildArchiveOutputStream(OutputStream outs) {
        throw new NotImplementedException();
    }

    @Override
    public ArchiveEntry buildArchiveEntry(String name, long size) {
        throw new NotImplementedException();
    }
}
