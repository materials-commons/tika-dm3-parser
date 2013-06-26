package org.apache.tika.parser.image;

public class MetaTuple
{
    private final String name;
    private final String value;

    public MetaTuple(String name, String value)
    {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

}
