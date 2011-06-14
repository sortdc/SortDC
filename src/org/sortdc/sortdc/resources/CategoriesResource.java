package org.sortdc.sortdc.resources;

import com.sun.jersey.api.NotFoundException;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.resources.dto.CategoriesDTO;
import org.sortdc.sortdc.resources.dto.CategoryDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CategoriesResource {

    private Classifier classifier;

    public CategoriesResource(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Retrieves list of all classifier's categories
     * 
     * @return
     */
    @GET
    public CategoriesDTO get() {
        CategoriesDTO categories_dto = new CategoriesDTO(this.classifier.getName());
        for (String category_id : this.classifier.getCategories().keySet()) {
            CategoryDTO category_dto = new CategoryDTO(this.classifier.getName(), category_id);
            categories_dto.add(category_dto);
        }
        return categories_dto;
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
     * Returns a new category resource
     * 
     * @param category_id
     * @return
     */
    @Path("/{category: [a-zA-Z0-9_-]+}")
    public CategoryResource getCategoryResource(@PathParam("category") String category_id) {
        Map<String, Category> categories = this.classifier.getCategories();
        if (!categories.containsKey(category_id)) {
            throw new NotFoundException();
        }
        return new CategoryResource(this.classifier, categories.get(category_id));
    }
}
