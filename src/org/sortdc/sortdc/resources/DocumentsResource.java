package org.sortdc.sortdc.resources;

import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Log;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.database.ObjectNotFoundException;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DocumentsResource {

    private Classifier classifier;

    public DocumentsResource(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Retrieves list of all documents of a category
     * 
     * @return
     */
    @GET
    public void get() {
        // TODO
    }
    
    /**
     * Adds a new document
     * 
     * @return
     */
    @POST
    public void post() {
        // TODO
    }
    
    /**
     * Deletes all classifier's categories and documents
     * 
     * @return
     */
    @DELETE
    public void delete() {
        // TODO
    }

    /**
     * Returns a new document resource
     * 
     * @param document_id
     * @return
     */
    @Path("/{document: [a-zA-Z0-9_-]+}")
    public DocumentResource getDocumentResource(@PathParam("document") String document_id) {
        Document document;
        try {
            document = this.classifier.findDocumentByName(document_id);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        return new DocumentResource(this.classifier, document);
    }
}
