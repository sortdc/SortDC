package org.sortdc.sortdc.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.resources.dto.CategoryDTO;
import org.sortdc.sortdc.resources.dto.DocumentDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DocumentResource {

    private Classifier classifier;
    private Document document;

    public DocumentResource(Classifier classifier, Document document) {
        this.classifier = classifier;
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
        String classifier_id = this.classifier.getName();
        DocumentDTO document_dto = new DocumentDTO(classifier_id, document.getName());
        document_dto.setCategory(new CategoryDTO(classifier_id, document.getCategoryId()));
        document_dto.setTokens(document.getWordsOccurrences());
        return document_dto;
    }
    
    /**
     * Update a document
     * 
     * @return
     */
    @PUT
    public void put() {
        // TODO
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
