package org.apache.tika.parser.image;

import java.util.List;

public enum DM3TagFormatReader
{
    GROUP
    {
        @Override
        public List<MetaTuple> readTagFormatEntry(EasyStreamReader esr)
        {
            return null;
        }
    },
    
    DATA
    {
        @Override
        public List<MetaTuple> readTagFormatEntry(EasyStreamReader esr)
        {
            return null;
        }
    };
    
    public abstract List<MetaTuple> readTagFormatEntry(EasyStreamReader esr);
}
