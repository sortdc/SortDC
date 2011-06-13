package org.sortdc.sortdc.resources;

import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Config;
import org.sortdc.sortdc.resources.dto.ClassifierDTO;
import org.sortdc.sortdc.resources.dto.ClassifiersDTO;

public class ClassifierResource {

    /**
     * Retrieves list of all classifiers
     * 
     * @return
     * @throws Exception 
     */
    @GET
    public List<ClassifierDTO> getAll() throws Exception {
        Map<String, Classifier> classifiers = Config.getInstance().getClassifiers();
        ClassifiersDTO classifiers_dto = new ClassifiersDTO();
        for (String classifier_id : classifiers.keySet()) {
            classifiers_dto.add(new ClassifierDTO(classifier_id));
        }
        return classifiers_dto;
    }

    /**
     * Retrieves information of a classifier
     * 
     * @param classifier_id
     * @return
     * @throws Exception 
     */
    @Path("/{classifier: [a-zA-Z0-9_-]+}")
    @GET
    public ClassifierDTO get(@PathParam("classifier") String classifier_id) throws Exception {
        Map<String, Classifier> classifiers = Config.getInstance().getClassifiers();
        if (!classifiers.containsKey(classifier_id)) {
            throw new WebApplicationException(404);
        }

        Classifier classifier = classifiers.get(classifier_id);
        ClassifierDTO classifier_dto = new ClassifierDTO(classifier_id);
        classifier_dto.enableCategories_href(true);
        return classifier_dto;
    }

    /**
     * Returns a new category resource
     * @param classifier_id
     * @return
     * @throws Exception 
     */
    @Path("/{classifier: [a-zA-Z0-9_-]+}/categories")
    public CategoryResource getCategoryResource(@PathParam("classifier") String classifier_id) throws Exception {
        Map<String, Classifier> classifiers = Config.getInstance().getClassifiers();
        if (!classifiers.containsKey(classifier_id)) {
            throw new WebApplicationException(404);
        }

        Classifier classifier = classifiers.get(classifier_id);
        return new CategoryResource(classifier);
    }
}
