package org.apache.tika.parser.image;

import java.util.List;

public enum DM3TagTypeReader
{
    SHORT
    {
      @Override
      public List<MetaTuple> readTag(EasyStreamReader esr)
      {
          return null;
      }
    };
    
    public abstract List<MetaTuple> readTag(EasyStreamReader esr);

}
