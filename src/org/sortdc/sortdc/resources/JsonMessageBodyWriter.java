package org.sortdc.sortdc.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonMessageBodyWriter implements MessageBodyWriter {

    @Override
    public long getSize(Object obj, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class type, Type genericType,
            Annotation annotations[], MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(Object target, Class type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap httpHeaders, OutputStream outputStream)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(Feature.INDENT_OUTPUT, true);
        mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.writeValue(outputStream, target);
    }
}
