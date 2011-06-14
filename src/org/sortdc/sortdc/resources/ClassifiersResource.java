package org.sortdc.sortdc.resources;

import com.sun.jersey.api.NotFoundException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Config;
import org.sortdc.sortdc.Log;
import org.sortdc.sortdc.resources.dto.ClassifierDTO;
import org.sortdc.sortdc.resources.dto.ClassifiersDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ClassifiersResource {

    /**
     * Retrieves list of all classifiers
     * 
     * @return
     */
    @GET
    public List<ClassifierDTO> get() {
        Map<String, Classifier> classifiers;
        try {
            classifiers = Config.getInstance().getClassifiers();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        ClassifiersDTO classifiers_dto = new ClassifiersDTO();
        for (String classifier_id : classifiers.keySet()) {
            classifiers_dto.add(new ClassifierDTO(classifier_id));
        }
        return classifiers_dto;
    }

    /**
     * Returns a new classifier resource
     * 
     * @param classifier_id
     * @return
     */
    @Path("/{classifier: [a-zA-Z0-9_-]+}")
    public ClassifierResource getClassifierResource(@PathParam("classifier") String classifier_id) {
        Map<String, Classifier> classifiers;
        try {
            classifiers = Config.getInstance().getClassifiers();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(500);
        }
        if (!classifiers.containsKey(classifier_id)) {
            throw new NotFoundException();
        }
        return new ClassifierResource(classifiers.get(classifier_id));
    }
}
