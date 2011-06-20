package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "category")
public class CategoryDTO {

    @XmlAttribute
    public String id;
    private String classifier_id;
    @XmlElement
    public Float likelihood;

    public CategoryDTO() {
    }

    public CategoryDTO(String classifier_id, String id) {
        this.id = id;
        this.classifier_id = classifier_id;
    }

    @XmlAttribute
    public String getHref() {
        if (this.id == null || this.classifier_id == null) {
            return null;
        }
        try {
            return Config.getInstance().getWebserviceURI() + "classifiers/" + this.classifier_id + "/categories/" + this.id;
        } catch (Exception e) {
            return null;
        }
    }
}
