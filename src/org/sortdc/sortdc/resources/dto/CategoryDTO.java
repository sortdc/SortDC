package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "category")
public class CategoryDTO {

    private String id;
    private String href;

    public CategoryDTO() {
    }

    public CategoryDTO(String classifier_id, String id) {
        this.id = id;
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + classifier_id + "/categories/" + id;
        } catch (Exception e) {
        }
    }

    @XmlAttribute
    public String getId() {
        return this.id;
    }

    @XmlAttribute
    public String getHref() {
        return this.href;
    }
}
