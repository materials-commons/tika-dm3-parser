package org.apache.tika.parser.image;

import java.nio.ByteOrder;

public enum DM3SimpleTagTypeReader
{
    SHORT(2)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readShort(order));
        }
    },
    
    LONG(3)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readInt(order));
        }
    },
    
    USHORT(4)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return SHORT.readValue(order, streamReader);
        }
    },
    
    ULONG(5)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return LONG.readValue(order, streamReader);
        }
    },
    
    FLOAT(6)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readFloat(order));
        }
    },
    
    DOUBLE(7)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readDouble(order));
        }
    },
    
    BOOLEAN(8)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readBoolean());
        }
    },
    
    CHAR(9)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return String.valueOf(streamReader.readChar());
        }
    },
    
    UNKNOWN(-1)
    {
        @Override
        public String readValue(ByteOrder order, EasyStreamReader streamReader)
        {
            return null;
        }
    };

    public abstract String readValue(ByteOrder order, EasyStreamReader streamReader);

    private final int value;

    private DM3SimpleTagTypeReader(int value)
    {
        this.value = value;
    }

    public static DM3SimpleTagTypeReader toSimpleTagTypeReader(int value)
    {
        for (DM3SimpleTagTypeReader simpleTagTypeReader : DM3SimpleTagTypeReader.values())
        {
            if (simpleTagTypeReader.value == value)
            {
                return simpleTagTypeReader;
            }
        }

        return UNKNOWN;
    }

}
