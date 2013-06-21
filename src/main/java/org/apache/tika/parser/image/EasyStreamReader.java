package org.apache.tika.parser.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

public class EasyStreamReader
{ 
    private final InputStream is;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    public EasyStreamReader(InputStream is)
    {
        this.is = is;
    }
    
    public EasyStreamReader(InputStream is, ByteOrder byteOrder)
    {
        this.is = is;
        this.byteOrder = byteOrder;
    }
    
    public void setByteOrder(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
    }
    
    public InputStream getInputStream()
    {
        return this.is;
    }
    
    public int readInt()
    {
        return readInt(byteOrder);
    }
    
    public int readInt(ByteOrder order)
    {
        return readIntoByteBuffer(4).order(order).getInt();
    }
    
    public short readShort()
    {
        return readShort(byteOrder);
    }
    
    public short readShort(ByteOrder order)
    {
        return readIntoByteBuffer(2).order(order).getShort();
    }
    
    public String readString(int length)
    {
        byte[] bytes = new byte[length];
        readFromStream(bytes, 0, length);
        String s = null;
        try
        {
            s = new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return s;
    }
    
    
    public byte readByte()
    {
        byte[] b = new byte[1];
        readFromStream(b, 0, 1);
        return b[0];
    }
    
    public void skipBytes(int numberOfBytes)
    {
        byte bytes[] = new byte[numberOfBytes];
        readFromStream(bytes, 0, numberOfBytes);
    }
    
    private ByteBuffer readIntoByteBuffer(int numberOfBytes)
    {
        byte[] bytes = new byte[numberOfBytes];
        readFromStream(bytes, 0, numberOfBytes);
        return ByteBuffer.wrap(bytes);
    }
    
    private int readFromStream(byte[] bytes, int offset, int length)
    {
        try
        {
            return is.read(bytes, offset, length);
        }
        catch (IOException e)
        {
            return 0;
        }
    }
}
