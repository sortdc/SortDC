package org.sortdc.sortdc.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Log;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.resources.dto.CategoryDTO;
import org.sortdc.sortdc.resources.dto.DocumentDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DocumentResource {

    private Classifier classifier;
    private Category category;
    private Document document;

    public DocumentResource(Classifier classifier, Category category, Document document) {
        this.classifier = classifier;
        this.category = category;
        this.document = document;
    }

    /**
     * Retrieves information of a category
     * 
     * @param category_id
     * @return
     */
    @GET
    public DocumentDTO get() {
        String classifier_id = this.classifier.getId();
        DocumentDTO document_dto = new DocumentDTO(classifier_id, this.document.getId());
        document_dto.setCategory(new CategoryDTO(classifier_id, this.category.getId()));
        document_dto.setTokens(this.document.getTokensOccurrences());
        return document_dto;
    }

    /**
     * Update a document
     * 
     * @return
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public DocumentDTO put(String text) {
        try {
            this.classifier.train(this.document, text);
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        return this.get();
    }

    /**
     * Deletes document
     * 
     * @return
     */
    @DELETE
    public void delete() {
        // TODO
    }
}
