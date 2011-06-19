package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "classifier")
public class ClassifierDTO {

    @XmlAttribute
    public String id;
    @XmlAttribute
    public String href;
    @XmlElement
    public CategoriesDTO categories;

    public ClassifierDTO() {
    }

    public ClassifierDTO(String id) {
        this.id = id;
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + id;
        } catch (Exception e) {
        }
    }
}
