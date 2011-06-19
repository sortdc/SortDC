package org.sortdc.sortdc.resources.dto;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "categories")
public class CategoriesDTO extends ArrayList<CategoryDTO> {

    @XmlAttribute
    public String href;

    public CategoriesDTO() {
    }

    public CategoriesDTO(String classifier_id) {
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/categories";
        } catch (Exception e) {
        }
    }

    @XmlElementRef(type = CategoryDTO.class)
    public List<CategoryDTO> getList() {
        return (List<CategoryDTO>) this;
    }
}
