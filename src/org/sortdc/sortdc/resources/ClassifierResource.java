package org.sortdc.sortdc.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.resources.dto.CategoriesDTO;
import org.sortdc.sortdc.resources.dto.ClassifierDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ClassifierResource {

    private Classifier classifier;

    public ClassifierResource(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Retrieves information of a classifier
     * 
     * @return
     */
    @GET
    public ClassifierDTO get() {
        String classifier_id = this.classifier.getName();
        ClassifierDTO classifier_dto = new ClassifierDTO(classifier_id);
        classifier_dto.setCategories(new CategoriesDTO(classifier_id));
        return classifier_dto;
    }

    /**
     * Returns a new categories resource
     * 
     * @return
     */
    @Path("/categories")
    public CategoriesResource getCategoriesResource() {
        return new CategoriesResource(this.classifier);
    }

    /**
     * Returns a new documents resource
     * 
     * @return
     */
    @Path("/documents")
    public DocumentsResource getDocumentsResource() {
        return new DocumentsResource(this.classifier);
    }
}
