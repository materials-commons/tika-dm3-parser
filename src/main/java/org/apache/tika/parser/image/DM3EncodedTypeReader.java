package org.apache.tika.parser.image;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public enum DM3EncodedTypeReader
{
    SIMPLE(1)
    {
        @Override
        public MetaTuple readEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            DM3SimpleTagTypeReader simpleTagTypeReader = DM3SimpleTagTypeReader
                    .toSimpleTagTypeReader(streamReader.readInt());
            String value = simpleTagTypeReader.readValue(order, streamReader);
            return new MetaTuple(label, value);
        }
    },

    STRING(2)
    {
        @Override
        public MetaTuple readEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            DM3TagType tagType = DM3TagType.toTagType(streamReader.readInt());
            if (tagType != DM3TagType.STRING)
            {
                throw new IllegalStateException("Unexpected tag: " + tagType);
            }

            int length = streamReader.readInt();
            String value = streamReader.readString(length);
            return new MetaTuple(label, value);
        }
    },

    ARRAY(3)
    {
        @Override
        public MetaTuple readEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            /*
             * This could be data or other binary data. If it is skip it.
             */
            DM3TagType tagType = DM3TagType.toTagType(streamReader.readInt());
            int length = streamReader.readInt();
            if (tagType == DM3TagType.OCTET || "Data".equals(label))
            {
                streamReader.skipBytes(length * tagType.numBytes());
                return null;
            }
            else
            {
                /*
                 * This is an array of shorts, first byte is a character, second
                 * is a 0. So we read as string removing \0 in between
                 * characters.
                 */
                String value = removeNulls(streamReader.readString(length * 2));
                return new MetaTuple(label, value);
            }
        }

        private String removeNulls(String in)
        {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < in.length(); i++)
            {
                if (in.charAt(i) != 0)
                {
                    buf.append(in.charAt(i));
                }
            }

            return buf.toString().trim();
        }
    },

    COMPLEX(-1)
    {
        @Override
        public MetaTuple readEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            DM3TagType tagType = DM3TagType.toTagType(streamReader.readInt());
            if (tagType == DM3TagType.STRUCT)
            {
                return readStructEntry(label, streamReader, order);
            }
            else if (tagType == DM3TagType.ARRAY)
            {
                return readArrayEntry(label, streamReader, order);
            }
            else
            {
                // Do something here
                return null;
            }
        }
        
        private MetaTuple readStructEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            /*
             *  Skip first 4 bytes (length of data area. We will know how much to read from the
             *  number of fields, and the size of each field.
             */
            streamReader.skipBytes(4);
            int numberOfFields = streamReader.readInt();
            // Skip unused 4 bytes
            streamReader.skipBytes(4);
            List<String> values = new ArrayList<>();
            for (int i = 0; i < numberOfFields; i++)
            {
                DM3SimpleTagTypeReader simpleTagTypeReader = DM3SimpleTagTypeReader
                        .toSimpleTagTypeReader(streamReader.readInt());
                String value = simpleTagTypeReader.readValue(order, streamReader);
                values.add(value);
            }
            /*
             * Read last entry, now we need to skip forward some amount to the next tag.
             */
            skipToNextTag(streamReader);
            
            String metaValue = StringUtils.join(values, ",");
            return new MetaTuple(label, metaValue);
        }
        
        private void skipToNextTag(EasyStreamReader streamReader)
        {
            byte[] bytesToPeek = peek(streamReader, 28);
            final int[] jumps = { 4, 3, 3, 5, 5, 3 };
            int amountToSkip = 0;
            for (int i = 0; i < jumps.length; i++)
            {
                amountToSkip += jumps[i] + 1;
                byte b = bytesToPeek[amountToSkip-1];
                DM3TagEntryFormat tagEntryFormat = DM3TagEntryFormat.toEntryType(b);
                //System.out.println("in loop amountToSkip = " + amountToSkip + "/b = " + b);
                if (tagEntryFormat != DM3TagEntryFormat.UNKNOWN)
                {
                    break;
                }
            }
            
            //System.out.println("Skipping " + (amountToSkip - 1));
            streamReader.skipBytes(amountToSkip - 1);
            //System.out.println("checking stream location = " + esr.readByte());
        }
        
        private byte[] peek(EasyStreamReader streamReader, int bytes)
        {
            byte[] buffer = new byte[bytes];
            try
            {
                streamReader.getInputStream().peek(buffer);
            }
            catch (IOException e)
            {
            }
            
            return buffer;
        }
        
        private MetaTuple readArrayEntry(String label, EasyStreamReader streamReader, ByteOrder order)
        {
            DM3TagType tagType = DM3TagType.toTagType(streamReader.readInt());
            /*
             * Should be STRUCT (15)
             */
            if (tagType != DM3TagType.STRUCT)
            {
                throw new IllegalStateException("Type not exected: " + tagType);
            }
            else
            {
                streamReader.skipBytes(4);
                int numberOfFields = streamReader.readInt();
                List<DM3SimpleTagTypeReader> dataTypeReaders = getDataTypeReaders(streamReader, numberOfFields);
                String metavalue = getValues(dataTypeReaders, numberOfFields, order, streamReader);
                return new MetaTuple(label, metavalue);
            }
        }
        
        private List<DM3SimpleTagTypeReader> getDataTypeReaders(EasyStreamReader streamReader, int numberOfFields)
        {
            List<DM3SimpleTagTypeReader> dataTypeReaders = new ArrayList<>(numberOfFields);
            for (int i = 0; i < numberOfFields; i++)
            {
                streamReader.skipBytes(4);
                DM3SimpleTagTypeReader tagReader = DM3SimpleTagTypeReader.toSimpleTagTypeReader(streamReader.readInt());
                dataTypeReaders.set(i, tagReader);
            }
            return dataTypeReaders;
        }
        
        private String getValues(List<DM3SimpleTagTypeReader> dataTypeReaders, int numberOfFields, ByteOrder order, EasyStreamReader streamReader)
        {
            int length = streamReader.readInt();
            List<String> values = new ArrayList<>();
            for (int k = 0; k < length; k++)
            {
                for (int q = 0; q < numberOfFields; q++)
                {
                    DM3SimpleTagTypeReader simpleTagTypeReader = dataTypeReaders.get(q);
                    String value = simpleTagTypeReader.readValue(order, streamReader);
                    values.add(value);
                }
            }
            return StringUtils.join(values, ",");
        }
    };

    public abstract MetaTuple readEntry(String label, EasyStreamReader streamReader, ByteOrder order);

    private final int value;

    private DM3EncodedTypeReader(int value)
    {
        this.value = value;
    }

    public static DM3EncodedTypeReader toEncodedTypeReader(int value)
    {
        for (DM3EncodedTypeReader encodedTypeReader : DM3EncodedTypeReader.values())
        {
            if (encodedTypeReader.value == value)
            {
                return encodedTypeReader;
            }
        }

        return DM3EncodedTypeReader.COMPLEX;
    }
}
