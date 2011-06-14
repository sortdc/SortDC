package org.sortdc.sortdc.resources.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.sortdc.sortdc.Config;

@XmlRootElement(name = "classifier")
public class ClassifierDTO {

    private String id;
    private String href;
    private CategoriesDTO categories;

    public ClassifierDTO() {
    }

    public ClassifierDTO(String id) {
        this.id = id;
        try {
            this.href = Config.getInstance().getWebserviceURI() + "classifiers/" + id;
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

    @XmlElement
    public CategoriesDTO getCategories() {
        return this.categories;
    }

    public void setCategories(CategoriesDTO categories) {
        this.categories = categories;
    }
}
