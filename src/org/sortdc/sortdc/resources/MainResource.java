package org.sortdc.sortdc.resources;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class MainResource {

    /**
     * Returns a new classifier resource
     * 
     * @return
     */
    @Path("/classifiers")
    public ClassifiersResource getClassifierResource() {
        return new ClassifiersResource();
    }
}
