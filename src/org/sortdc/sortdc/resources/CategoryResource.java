package org.sortdc.sortdc.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.resources.dto.CategoryDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CategoryResource {

    private Classifier classifier;
    private Category category;

    public CategoryResource(Classifier classifier, Category category) {
        this.classifier = classifier;
        this.category = category;
    }

    /**
     * Retrieves information of the category
     * 
     * @return
     */
    @GET
    public CategoryDTO get() {
        CategoryDTO category_dto = new CategoryDTO(this.classifier.getId(), this.category.getId());
        return category_dto;
    }

    /**
     * Deletes category and its documents
     * 
     * @return
     */
    @DELETE
    public void delete() {
        // TODO
    }

    /**
     * Returns a new documents resource
     * 
     * @return
     */
    @Path("/documents")
    public DocumentsResource getDocuments() {
        return new DocumentsResource(this.classifier, this.category);
    }
}
