package org.sortdc.sortdc.resources.dto;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONConfiguration.NaturalBuilder;
import com.sun.jersey.api.json.JSONJAXBContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class[] types = {
        CategoriesDTO.class,
        CategoryDTO.class,
        ClassifiersDTO.class,
        ClassifierDTO.class,
        DocumentDTO.class,
        TokenDTO.class
    };

    public JAXBContextResolver() throws Exception {
        NaturalBuilder builder = JSONConfiguration.natural();
        builder.rootUnwrapping(true);
        builder.humanReadableFormatting(true);
        this.context = new JSONJAXBContext(builder.build(), types);
    }

    public JAXBContext getContext(Class<?> objectType) {
        for (Class type : types) {
            if (type == objectType) {
                return context;
            }
        }
        return null;
    }
}