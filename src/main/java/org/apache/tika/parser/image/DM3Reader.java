package org.apache.tika.parser.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class DM3Reader
{
    private static ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    private static final int DATA = 21;
    private static final int GROUP = 20;

    public static void main(String[] args) throws IOException
    {
        File f = new File("/home/gtarcea/Dropbox/transfers/materialscommons/4-bf-150k.dm3");
        InputStream is = new BufferedInputStream(new FileInputStream(f));
        EasyStreamReader esr = new EasyStreamReader(is);
        int version = esr.readInt();
        System.out.printf("version = %d%n", version);
        int sizeOfFile = esr.readInt();
        System.out.printf("sizeOfFile = %d%n", sizeOfFile);
        int endian = esr.readInt();
        DM3Reader.byteOrder = (endian == 1) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        System.out.printf("endian = %d%n", endian);
        // setByteOrder(esr, endian);
        esr.skipBytes(2);
        int numberOfTags = esr.readInt();
        System.out.printf("numberOfTags = %d%n", numberOfTags);
        readTags(esr, numberOfTags);
        is.close();
    }

    private static void readTags(EasyStreamReader esr, int numberOfTags)
    {
        readTag(esr);
//        for(int i = 0; i < numberOfTags; i++)
//        {
//            readTag(esr);
//        }
    }
    
    private static void readTag(EasyStreamReader esr)
    {
        byte tagType = esr.readByte();
        System.out.println("tagType = " + DM3TagEntryFormat.toEntryType(tagType));
        short lengthToRead = esr.readShort();
        System.out.printf("lengthToRead = %d%n", lengthToRead);
//        byte b = esr.readByte();
//        System.out.println("b = " + b);
        String label = esr.readString(lengthToRead);
        System.out.println("label = " + label);
        esr.skipBytes(4);
        int n = esr.readInt(byteOrder);
        System.out.printf("n = %d%n", n);
    }
}
