package org.sortdc.sortdc.resources;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonMessageBodyReader implements MessageBodyReader {

    @Override
    public boolean isReadable(Class type, Type genericType,
            Annotation annotations[], MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, InputStream inputStream)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, type);
    }
}
