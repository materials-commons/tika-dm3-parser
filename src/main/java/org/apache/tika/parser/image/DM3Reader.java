package org.apache.tika.parser.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

public class DM3Reader
{
    private EasyStreamReader streamReader;
    private DM3Header header;
    
    public DM3Reader(TikaInputStream stream)
    {
        this.streamReader = new EasyStreamReader(stream);
    }
    
    public Metadata parse()
    {
        readHeader();
        // First entry is always a Tag Group
        List<MetaTuple> metatuples = new ArrayList<>();
        DM3TagFormatReader.GROUP.readTagFormatEntry(null, streamReader, header.getByteOrder(), metatuples);
        Metadata metadata = createMetadata(metatuples);
        return metadata;
    }
    
    public DM3Header getHeader()
    {
        return this.header;
    }
    
    private void readHeader()
    {
        int version = streamReader.readInt();
        int sizeOfFile = streamReader.readInt();
        int endian = streamReader.readInt();
        ByteOrder byteOrder = (endian == 1) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;       
        this.header = new DM3Header(version, sizeOfFile, byteOrder);
    }

    private Metadata createMetadata(List<MetaTuple> metaTuples)
    {
        Metadata metadata = new Metadata();
        for (MetaTuple metaTuple : metaTuples)
        {
            metadata.set(metaTuple.getName(), metaTuple.getValue());
        }
        return metadata;
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
        int lengthOfDefinition = esr.readInt();
        System.out.printf("lengthOfDefinition = %d%n", lengthOfDefinition);
        int dataType = esr.readInt();
        System.out.println("dataType = " + dataType);
        readArray(esr);
    }
    
    private static void readArray(EasyStreamReader esr)
    {
        esr.skipBytes(4);
        int numFields = esr.readInt();
        System.out.println("numFields = " + numFields);
        esr.skipBytes(4);
        for (int i = 0; i < numFields; i++)
        {
            int dataType = esr.readInt();
            System.out.println("dataType = " + dataType);
            readDataTypeValue(esr, dataType);
        }
        skipToNextTag(esr);
    }
    
    private static void readDataTypeValue(EasyStreamReader esr, int dataType)
    {
        DM3TagType tagType = DM3TagType.toTagType(dataType);
        System.out.println("  tagType = " + tagType);
        int numBytes = tagType.numBytes();
        System.out.println("  numBytes = " + numBytes);
        esr.readBytes(numBytes);
    }
    
    private static void skipToNextTag(EasyStreamReader esr)
    {
        byte[] bytesToPeek = peek(esr, 28);
        final int[] jumps = { 4, 3, 3, 5, 5, 3 };
        int amountToSkip = 0;
        for (int i = 0; i < jumps.length; i++)
        {
            amountToSkip += jumps[i] + 1;
            byte b = bytesToPeek[amountToSkip-1];
            System.out.println("in loop amountToSkip = " + amountToSkip + "/b = " + b);
            if (b == 20 || b == 21)
            {
                break;
            }
        }
        
        System.out.println("Skipping " + (amountToSkip - 1));
        esr.skipBytes(amountToSkip - 1);
        //System.out.println("checking stream location = " + esr.readByte());
    }
    
    private static byte[] peek(EasyStreamReader esr, int bytes)
    {
        byte[] buffer = new byte[28];
        try
        {
            esr.getInputStream().peek(buffer);
        }
        catch (IOException e)
        {
        }
        
        return buffer;
    }
    
    public static void main(String[] args) throws IOException
    {
        File f = new File("/home/gtarcea/Dropbox/transfers/materialscommons/4-bf-150k.dm3");
        TikaInputStream is = TikaInputStream.get(new BufferedInputStream(new FileInputStream(f)));
        EasyStreamReader esr = new EasyStreamReader(is);
        int version = esr.readInt();
        System.out.printf("version = %d%n", version);
        int sizeOfFile = esr.readInt();
        System.out.printf("sizeOfFile = %d%n", sizeOfFile);
        int endian = esr.readInt();
        //DM3Reader.byteOrder = (endian == 1) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        System.out.printf("endian = %d%n", endian);
        // setByteOrder(esr, endian);
        esr.skipBytes(2);
        int numberOfTags = esr.readInt();
        System.out.printf("numberOfTags = %d%n", numberOfTags);
        readTags(esr, numberOfTags);
        is.close();
    }
}
