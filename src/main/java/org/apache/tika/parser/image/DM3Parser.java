package org.apache.tika.parser.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DM3Parser extends AbstractParser
{
    private static final long serialVersionUID = 5054388050889170708L;
    
    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.image("x-dm3"));

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context)
    {
        return SUPPORTED_TYPES;
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException
    {
        TikaInputStream tikaStream = TikaInputStream.get(stream);
        parseDM3Stream(tikaStream, handler, metadata, context);
    }

    private void parseDM3Stream(TikaInputStream tikaStream, ContentHandler handler, Metadata metadata,
            ParseContext context)
    {
        
    }

}
