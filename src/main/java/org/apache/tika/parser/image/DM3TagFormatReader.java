package org.apache.tika.parser.image;

import java.nio.ByteOrder;
import java.util.List;

public enum DM3TagFormatReader
{
    GROUP(20)
    {
        @Override
        public void readTagFormatEntry(String label, EasyStreamReader streamReader, ByteOrder order, List<MetaTuple> metatuples)
        {
            /*
             ** Byte 1: is group sorted?
             ** Byte 2: is group open?
             ** Skip them both
             */
            streamReader.skipBytes(2);
            int numberOfTags = streamReader.readInt();
            readTagEntries(numberOfTags, streamReader, order, metatuples);
        }
        
        private void readTagEntries(int numberOfTags, EasyStreamReader streamReader, ByteOrder order, List<MetaTuple> metatuples)
        {
            for (int i = 0; i < numberOfTags; i++)
            {
                readTagEntry(streamReader, order, metatuples);
            }
        }
        
        private void readTagEntry(EasyStreamReader streamReader, ByteOrder order, List<MetaTuple> metatuples)
        {
            byte tagEntryType = streamReader.readByte();
            short labelLength = streamReader.readShort();
            String label = streamReader.readString(labelLength);
            DM3TagFormatReader.toTagFormatReader(tagEntryType).readTagFormatEntry(label, streamReader, order, metatuples);
        }
    },
    
    DATA(21)
    {
        @Override
        public void readTagFormatEntry(String label, EasyStreamReader streamReader, ByteOrder order, List<MetaTuple> metatuples)
        {
            // First 4 bytes = '%%%%'
            streamReader.skipBytes(4);
            int lengthOfDefinition = streamReader.readInt();
            DM3EncodedTypeReader encodedTypeReader = DM3EncodedTypeReader.toEncodedTypeReader(lengthOfDefinition);
            MetaTuple tuple = encodedTypeReader.readEntry(label, streamReader, order);
            if (tuple != null)
            {
                metatuples.add(tuple);
            }
        }
    };
    
    public abstract void readTagFormatEntry(String label, EasyStreamReader streamReader, ByteOrder order, List<MetaTuple> metatuples);
    
    private final byte value;
    
    private DM3TagFormatReader(int value)
    {
        this.value = (byte) value;
    }
    
    public static DM3TagFormatReader toTagFormatReader(byte value)
    {
        for (DM3TagFormatReader formatReader : DM3TagFormatReader.values())
        {
            if (formatReader.value == value)
            {
                return formatReader;
            }
        }
        
        throw new IllegalStateException("Unknown format: " + value);
    }
}
