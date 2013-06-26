package org.apache.tika.parser.image;

public enum DM3TagType
{
    SHORT(2, 2), LONG(3, 4), USHORT(4, 2), UNLONG(5, 2), FLOAT(6, 4), DOUBLE(7, 8),
    BOOLEAN(8, 1), CHAR(9, 1), OCTET(10, 1), STRUCT(15, -1), STRING(18, -1), ARRAY(20, -1),
    UNKNOWN(0, 0);
    
    private final int value;
    private final int numBytes;
    
    private DM3TagType(int value, int numBytes)
    {
        this.value = value;
        this.numBytes = numBytes;
    }
    
    public int numBytes()
    {
        return this.numBytes;
    }
    
    public static DM3TagType toTagType(int value)
    {
        for(DM3TagType tagType : DM3TagType.values())
        {
            if (tagType.value == value)
            {
                return tagType;
            }
        }
        
        return UNKNOWN;
    }
}
