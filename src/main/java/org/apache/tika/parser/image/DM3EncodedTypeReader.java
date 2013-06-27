package org.apache.tika.parser.image;

import java.nio.ByteOrder;

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
                // do something here...
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
            return new MetaTuple(null, null);
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
