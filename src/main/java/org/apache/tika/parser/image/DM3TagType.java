package org.apache.tika.parser.image;

public enum DM3TagType
{
    SHORT(2), LONG(3), USHORT(4), UNLONG(5), FLOAT(6), DOUBLE(7),
    BOOLEAN(8), CHAR(9), OCTET(10), STRUCT(15), STRING(18), ARRAY(20),
    UNKNOWN(0);
    
    private final int value;
    
    private DM3TagType(int value)
    {
        this.value = value;
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
