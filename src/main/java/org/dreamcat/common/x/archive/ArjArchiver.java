package org.dreamcat.common.x.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.arj.ArjArchiveInputStream;
import org.dreamcat.common.exception.NotImplementedException;

import java.io.InputStream;
import java.io.OutputStream;

// only unarchive is support
public class ArjArchiver implements Archiver {
    @Override
    public ArchiveInputStream buildArchiveInputStream(InputStream ins) throws ArchiveException {
        return new ArjArchiveInputStream(ins);
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
