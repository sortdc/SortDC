package org.sortdc.sortdc.resources;

import com.sun.jersey.api.NotFoundException;
import javax.ws.rs.Consumes;
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
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.database.ObjectNotFoundException;
import org.sortdc.sortdc.resources.dto.CategoryDTO;
import org.sortdc.sortdc.resources.dto.DocumentDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DocumentsResource {

    private Classifier classifier;
    private Category category;

    public DocumentsResource(Classifier classifier) {
        this.classifier = classifier;
    }

    public DocumentsResource(Classifier classifier, Category category) {
        this.classifier = classifier;
        this.category = category;
    }

    /**
     * Retrieves list of all documents
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
    @Consumes(MediaType.TEXT_PLAIN)
    public DocumentDTO post(String text) {
        if (this.category == null) {
            throw new WebApplicationException(405);
        }
        Document document;
        try {
            document = this.classifier.train((String) null, text, this.category.getId());
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        String classifier_id = this.classifier.getId();
        DocumentDTO document_dto = new DocumentDTO(classifier_id, document.getId());
        document_dto.setCategory(new CategoryDTO(classifier_id, this.category.getId()));
        document_dto.setTokens(document.getWordsOccurrences());
        return document_dto;
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
        if (this.category != null) {
            throw new NotFoundException();
        }
        Document document;
        try {
            document = this.classifier.findDocumentById(document_id);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        Category category;
        try {
            category = this.classifier.getCategory(document.getCategoryId());
        } catch (ObjectNotFoundException e) {
            System.out.println(e.getMessage());
            throw new NotFoundException();
        }
        return new DocumentResource(this.classifier, category, document);
    }
}
