package org.apache.tika.parser.image;

public enum DM3TagEntryFormat
{
    DATA(21), GROUP(20), UNKNOWN(0);
    
    private final byte value;
    
    private DM3TagEntryFormat(int value)
    {
        this.value = (byte) value;
    }
    
    public static DM3TagEntryFormat toEntryType(byte value)
    {
        for (DM3TagEntryFormat entryType : DM3TagEntryFormat.values())
        {
            if (entryType.value == value)
            {
                return entryType;
            }
        }
        
        return UNKNOWN;
    }
}
