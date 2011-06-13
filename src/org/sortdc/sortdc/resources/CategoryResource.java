package org.sortdc.sortdc.resources;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.resources.dto.CategoriesDTO;
import org.sortdc.sortdc.resources.dto.CategoryDTO;

public class CategoryResource {

    private Classifier classifier;

    public CategoryResource(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Retrieves list of all categories of a classifier
     * 
     * @return
     * @throws Exception 
     */
    @GET
    public CategoriesDTO getAll() throws Exception {
        CategoriesDTO categories_dto = new CategoriesDTO();
        for (String category_id : this.classifier.getCategories().keySet()) {
            CategoryDTO category_dto = new CategoryDTO(this.classifier.getName(), category_id);
            categories_dto.add(category_dto);
        }
        return categories_dto;
    }

    /**
     * Retrieves information of a category
     * 
     * @param category_id
     * @return
     * @throws Exception 
     */
    @Path("/{category: [a-zA-Z0-9_-]+}")
    @GET
    public CategoryDTO get(@PathParam("category") String category_id) throws Exception {
        Map<String, Category> categories = this.classifier.getCategories();
        if (!categories.containsKey(category_id)) {
            throw new WebApplicationException(404);
        }

        Category category = categories.get(category_id);
        CategoryDTO category_dto = new CategoryDTO(this.classifier.getName(), category_id);
        return category_dto;
    }
}
