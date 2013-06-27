package org.apache.tika.parser.image;

import java.nio.ByteOrder;

public class DM3Header
{
    private final int version;
    private final int sizeOfFile;
    private final ByteOrder byteOrder;

    public DM3Header(int version, int sizeOfFile, ByteOrder byteOrder)
    {
        super();
        this.version = version;
        this.sizeOfFile = sizeOfFile;
        this.byteOrder = byteOrder;
    }

    public int getVersion()
    {
        return version;
    }

    public int getSizeOfFile()
    {
        return sizeOfFile;
    }

    public ByteOrder getByteOrder()
    {
        return byteOrder;
    }

}
