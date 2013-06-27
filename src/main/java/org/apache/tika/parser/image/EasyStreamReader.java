package org.apache.tika.parser.image;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.tika.io.TikaInputStream;

public class EasyStreamReader
{
    private final TikaInputStream is;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public EasyStreamReader(TikaInputStream is)
    {
        this.is = is;
    }

    public EasyStreamReader(TikaInputStream is, ByteOrder byteOrder)
    {
        this.is = is;
        this.byteOrder = byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
    }

    public TikaInputStream getInputStream()
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

    public char readChar()
    {
        return readChar(byteOrder);
    }

    public char readChar(ByteOrder order)
    {
        return readIntoByteBuffer(1).order(order).getChar();
    }

    public float readFloat()
    {
        return readFloat(byteOrder);
    }

    public float readFloat(ByteOrder order)
    {
        return readIntoByteBuffer(4).order(order).getFloat();
    }

    public double readDouble()
    {
        return readDouble(byteOrder);
    }

    public double readDouble(ByteOrder order)
    {
        return readIntoByteBuffer(8).order(order).getDouble();
    }
    
    public boolean readBoolean()
    {
        return readByte() == 1 ? true : false;
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

    public ByteBuffer readBytes(int numberOfBytes)
    {
        return readIntoByteBuffer(numberOfBytes);
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
